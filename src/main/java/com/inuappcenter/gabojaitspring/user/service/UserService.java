package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.http.ConflictException;
import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.exception.http.UnauthorizedException;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.dto.*;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ContactService contactService;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;



    /**
     * 중복 유저이름 존재 여부 확인 |
     * 중복 유저이름 존재를 파악하고, 이미 사용중인 유저이름이면 409(Conflict)를 던진다. 만약 조회 중 에러가 발생하면 500(Internal Server
     * Error)를 던진다.
     */
    public void isExistingUsername(String username) {
        log.info("IN PROGRESS | 중복 유저이름 존재 여부 확인 At " + LocalDateTime.now() + " | " + username);
        userRepository.findByUsername(username)
                .ifPresent(u -> {
                    throw new ConflictException("이미 사용중인 아이디입니다");
                });
        log.info("COMPLETE | 중복 유저이름 존재 여부 확인 At " + LocalDateTime.now() + " | " + username);
    }

    /**
     * User 저장 |
     * User의 Contact를 조회한다. 조회되는 Contact의 이메일이 인증이 안됐을 경우 409(Conflict)를 던진다. 이메일 인증을 했을 경우 회원가입을
     * 진행하고 비밀번호는 인코드한다. 만약 User 정보 저장 중 에러가 발생하면 500(Internal Server Error)를 던진다.
     */
    public UserDefaultResponseDto save(UserSaveRequestDto request) {
        log.info("IN PROGRESS | User 저장 At " + LocalDateTime.now() + " | " + request.toString());
        Contact foundContact = contactService.findOneContact(request.getEmail());
        if (!foundContact.getIsVerified()) {
            throw new ConflictException("이메일 인증을 해주세요");
        }
        contactService.register(foundContact);
        try {
            User roleAssignedUser = assignAsUser(request.toEntity(foundContact));
            roleAssignedUser.setPassword(passwordEncoder.encode(roleAssignedUser.getPassword()));
            User insertedUser = userRepository.insert(roleAssignedUser);
            log.info("COMPLETE | User 저장 At " + LocalDateTime.now() + " | " + insertedUser);
            return new UserDefaultResponseDto(insertedUser);
        } catch (Exception e) {
            throw new InternalServerErrorException("유저 저장 중 에러 발생", e);
        }
    }

    /**
     * 사용자 역할 부여 |
     * User에게 사용자 역할을 부여한다.
     */
    public User assignAsUser(User user) {
        log.info("IN PROGRESS | 사용자 역할 부여 At " + LocalDateTime.now() + " | " + user.toString());
        user.addRole("USER");
        log.info("COMPLETE | 사용자 역할 부여 At " + LocalDateTime.now() + " | " + user);
        return user;
    }

    /**
     * 사용자 조회 |
     * User를 조회 한다. 조회가 되지 않은 User일 경우 401(Unauthorized)를 던진다.
     */
    public UserDefaultResponseDto findOneUser(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UnauthorizedException("유저 정보가 존재하지 않습니다");
        } else {
            return new UserDefaultResponseDto(user.get());
        }
    }

    /**
     * User 이메일로 조회 |
     * User를 이메일로 조회하여 해당 이메일에 아이디 정보를 보낸다. 조회가 되지 않은 User일 경우 401(Unauthorized)를 던진다.
     */
    public void findForgotUsernameByEmail(String email) {
        log.info("IN PROGRESS | User 이메일로 조회 At " + LocalDateTime.now() + " | " + email);
        Contact contact = contactService.findOneContact(email);
        userRepository.findByContact(contact.getEmail())
                .ifPresentOrElse((user) -> {
                    emailService.sendEmail(
                            email,
                            "가보자it 아이디 찾기",
                            user.getLegalName() + "님 안녕하세요!🙇🏻<br>해당 이메일로 가입된 아이디 정보입니다.",
                            user.getUsername()
                    );
                    log.info("COMPLETE | User 이메일로 조회 At " + LocalDateTime.now() + " | " + user);
                }, () -> {
                    throw new UnauthorizedException("유저 정보가 존재하지 않습니다");
                });
    }

    /**
     * User 이메일과 아이디로 비밀번호 초기화 |
     * User 이메일과 아이디를 받아 해당 User의 비밀번호를 초기화하여 관련 정보를 보낸다. 조회가 되지 않은 User일 경우 401(Unauthorized)를 던진다.
     */
    public void resetPasswordByEmailAndUsername(String username, String email) {
        log.info("IN PROGRESS | User 이메일과 아이디로 비밀번호 초기화 At "  + LocalDateTime.now() +
                " | email =" + email + ", username =" + username);
        userRepository.findByUsername(username)
                .ifPresentOrElse((user) -> {
                    if (user.getContact().getEmail().equals(email)) {
                        String temporaryPassword = generateTemporaryPassword();
                        try {
                            user.setPassword(passwordEncoder.encode(temporaryPassword));
                            userRepository.save(user);
                        } catch (Exception e) {
                            throw new InternalServerErrorException("User 임시 비밀번호 저장 중 에러", e);
                        }
                        emailService.sendEmail(
                                email,
                                "가보자it 비밀번호 찾기",
                                user.getLegalName() +
                                        "님 안녕하세요!🙇🏻<br>임시 비밀번호를 제공해 드립니다.<br>접속 후 비밀번호를 변경 해주세요.",
                                temporaryPassword
                        );
                        log.info("COMPLETE | User 이메일과 아이디로 비밀번호 초기화 At "  + LocalDateTime.now() +
                                " | email =" + email + ", username =" + username);
                    } else {
                        throw new UnauthorizedException("유저 정보가 존재하지 않습니다");
                    }
                }, () -> {
                    throw new UnauthorizedException("유저 정보가 존재하지 않습니다");
                });
    }

    /**
     * User 비밀번호 재설정 |
     * User 식별자로 정보를 조회해 현재 비밀번호를 비교하고 새 비밀번호와 새 비밀번호 재입력을 비교하여 비밀번호 재설정합니다. User 정보가 조회되지 않거나,
     * 현재 비밀번호를 틀리거나, 새 비밀번호와 새 비밀번호 재입력이 동일하지 않을시 401(Unauthorized)를 던진다. 새로운 비밀번호를 재설정시 에러가 발생하면
     * 500(Internal Server Error)를 던진다.
     */
    public void resetPassword(UserResetPasswordRequestDto request) {
        userRepository.findById(request.getUserId()).ifPresentOrElse(
                (user) -> {
                    if (passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        if (request.getNewPassword().equals(request.getNewPasswordReEntered())) {
                            try {
                                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                                userRepository.save(user);
                            } catch (Exception e) {
                                throw new InternalServerErrorException("유저 비밀번호 재설정 중 에러", e);
                            }
                        } else {
                            throw new UnauthorizedException("새 비밀번호와 새 비밀번호 재입력이 다릅니다");
                        }
                    } else {
                        throw new UnauthorizedException("현재 비밀번호가 틀렸습니다");
                    }
                }, () -> {
                    throw new UnauthorizedException("유저 정보가 존재하지 않습니다");
                }
        );
    }

    /**
     * User 전체 삭제 |
     * 배포 단계에서 삭제
     */
    public void deleteAll() {
        try {
            log.info("IN PROGRESS | User 전체 삭제 At " + LocalDateTime.now());
            userRepository.deleteAll();
        } catch (Exception e) {
            throw new InternalServerErrorException("User 전체 삭제 중 에러", e);
        }
        log.info("COMPLETE | User 전체 삭제 At " + LocalDateTime.now());
    }

    /**
     * 임시 비밀번호 생성 |
     * 숫자, 대문자 영문, 소문자 영문의 10가지 조합을 생성해 반환한다.
     */
    private String generateTemporaryPassword() {
        log.info("IN PROGRESS | 임시 비밀번호 생성 At " + LocalDateTime.now());
        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        log.info("COMPLETE | 임시 비밀번호 생성 At " + LocalDateTime.now() + " | " + sb);
        return sb.toString();
    }
}
