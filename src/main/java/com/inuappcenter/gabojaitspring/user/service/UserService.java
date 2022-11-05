package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.http.*;
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
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 중복 유저이름 존재 여부 확인 |
     * 중복 유저이름 존재를 파악하고, 이미 사용중인 유저이름이면 409(Conflict)를 던진다.
     */
    public void isExistingUsername(String username) {
        log.info("INITIALIZE | 중복 유저이름 존재 여부 확인 At " + LocalDateTime.now() + " | " + username);
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    if (!user.getIsDeactivated()) {
                        throw new ConflictException("이미 사용중인 아이디입니다");
                    }
                });
        log.info("COMPLETE | 중복 유저이름 존재 여부 확인 At " + LocalDateTime.now() + " | " + username);
    }

    /**
     * 중복 닉네임 존재 여부 확인 |
     * 중복 닉네임 존재를 파악하고, 이미 사용중인 닉네임이면 409(Conflict)를 던진다.
     */
    public void isExistingNickname(String nickname) {
        log. info("INITIALIZE | 중복 닉네임 존재 여부 확인 At " + LocalDateTime.now() + " | " + nickname);
        userRepository.findByNickname(nickname)
                .ifPresent(user -> {
                    if (!user.getIsDeactivated()) {
                        throw new ConflictException("이미 사용중인 닉네임입니다");
                    }
                });
        log. info("COMPLETE | 중복 닉네임 존재 여부 확인 At " + LocalDateTime.now() + " | " + nickname);
    }

    /**
     * 유저 저장 |
     * 유저의 정보를 저장한다. 해당 유저가 이메일 인증을 하지 않았을 경우 401(Unauthorized)를 던지고, 서버 에러가 발생하면
     * 500(Internal Server Error)을 던진다.
     */
    public UserDefaultResponseDto save(UserSaveRequestDto request) {
        log.info("INITIALIZE | 유저 저장 At " + LocalDateTime.now() + " | " + request.getUsername());
        Contact contact = contactService.findOneContact(request.getEmail());
        if (!contact.getIsVerified()) {
            throw new UnauthorizedException("이메일 인증을 해주세요");
        }
        isExistingUsername(request.getUsername());
        isExistingNickname(request.getNickname());
        contactService.register(contact);
        User roleAssignedUser = assignAsUser(request.toEntity(contact));
        roleAssignedUser.setPassword(passwordEncoder.encode(roleAssignedUser.getPassword()));
        try {
            User user = userRepository.save(roleAssignedUser);
            log.info("COMPLETE | 유저 저장 At " + LocalDateTime.now() + " | " + user.getUsername());
            return new UserDefaultResponseDto(user);
        } catch (Exception e) {
            throw new InternalServerErrorException("유저 저장 중 에러", e);
        }
    }

    /**
     * 유저 역할 부여 |
     * 유저에게 사용자 역할을 부여한다.
     */
    public User assignAsUser(User user) {
        log.info("INITIALIZE | 유저 역할 부여 At " + LocalDateTime.now() + " | " + user.getUsername());
        user.addRole("USER");
        log.info("COMPLETE | 유저 역할 부여 At " + LocalDateTime.now() + " | " + user.getUsername());
        return user;
    }

    /**
     * 유저 조회 |
     * 유저를 조회 한다. 조회가 되지 않거나 탈퇴한 유저일 경우 404(NotFound)를 던진다.
     */
    public UserDefaultResponseDto findOneUser(String id) {
        log.info("INITIALIZE | 유저 조회 At " + LocalDateTime.now() + " | " + id);
        Optional<User> user = userRepository.findById(id);
        if ((user.isPresent() && user.get().getIsDeactivated()) || user.isEmpty()) {
            throw new NotFoundException("존재하지 않는 유저입니다");
        }
        log.info("COMPLETE | 유저 조회 At " + LocalDateTime.now() + " | " + user.get().getUsername());
        return new UserDefaultResponseDto(user.get());
    }

    /**
     * 유저 조희 후 유저 엔티티 반환 |
     * 유저를 조회 하여 유저 엔티티로 반환한다. 조회가 되지 않거나 탈퇴한 유저일 경우 404(NotFound)를 던진다.
     */
    public User findUser(String id) {
        log.info("INITIALIZE | 유저 조희 후 유저 엔티티 반환 At " + LocalDateTime.now() + " | " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재하지 않은 유저입니다");
                });
        log.info("COMPLETE | 유저 조희 후 유저 엔티티 반환 At " + LocalDateTime.now() + " | " + user.getId());
        return user;
    }

    /**
     * 이메일로 아이디 찾기 |
     * 이메일로 유저를 조회하여 해당 이메일로 아이디를 보낸다. 조회가 되지 않거나 탈퇴한 유저일 경우 404(NotFound)를 던진다.
     */
    public void findForgotUsernameByEmail(String email) {
        log.info("INITIALIZE | 이메일로 아이디 찾기 At " + LocalDateTime.now() + " | " + email);
        Contact contact = contactService.findOneContact(email);
        userRepository.findByContact(contact.getEmail())
                .ifPresentOrElse(user -> {
                    if (user.getIsDeactivated()) {
                        throw new NotFoundException("존재하지 않는 유저입니다");
                    }
                    emailService.sendEmail(
                            email,
                            "[가보자it] 아이디 찾기",
                            user.getLegalName() + "님 안녕하세요!🙇🏻<br>해당 이메일로 가입된 아이디 정보입니다.",
                            user.getUsername()
                    );
                    log.info("COMPLETE | 이메일로 아이디 찾기 At " + LocalDateTime.now() + " | " + user.getUsername());
                }, () -> {
                    throw new NotFoundException("존재하지 않는 유저입니다");
                });
    }

    /**
     * 이메일과 아이디로 비밀번호 초기화 |
     * 유저 이메일과 아이디를 받아 해당 유저의 비밀번호를 초기화한다. 존재하지 않거나 탈퇴했거나 유저 아이디와 이메일이 동일하지 않을 경우 404(NotFound)를
     * 던지고, 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void resetPasswordByEmailAndUsername(String username, String email) {
        log.info("INITIALIZE | 이메일과 아이디로 비밀번호 초기화 At "  + LocalDateTime.now() +
                " | email = " + email + ", username = " + username);
        userRepository.findByUsername(username)
                .ifPresentOrElse(user -> {
                    if (user.getIsDeactivated()) {
                        throw new NotFoundException("존재하지 않는 유저입니다");
                    }
                    if (user.getContact().getEmail().equals(email)) {
                        String temporaryPassword = generateTemporaryPassword();
                        try {
                            user.setPassword(passwordEncoder.encode(temporaryPassword));
                            userRepository.save(user);
                        } catch (Exception e) {
                            throw new InternalServerErrorException("유저 임시 비밀번호 저장 중 에러", e);
                        }
                        emailService.sendEmail(
                                email,
                                "[가보자it] 비밀번호 찾기",
                                user.getLegalName() +
                                        "님 안녕하세요!🙇🏻<br>임시 비밀번호를 제공해 드립니다.<br>접속 후 비밀번호를 변경 해주세요.",
                                temporaryPassword
                        );
                        log.info("COMPLETE | 이메일과 아이디로 비밀번호 초기화 At "  + LocalDateTime.now() +
                                " | email = " + user.getContact().getEmail() + ", username = " + user.getUsername());
                    } else {
                        throw new NotFoundException("존재하지 않는 유저입니다");
                    }
                }, () -> {
                    throw new NotFoundException("존재하지 않는 유저입니다");
                });
    }

    /**
     * 닉네임 업데이트 |
     * 닉네임을 업데이트합니다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public String updateNickname(UserUpdateNicknameRequestDto request, String userId) {
        log.info("INITIALIZE | 닉네임 업데이트 At " + LocalDateTime.now() + " | " + userId);
        User user = findUser(userId);
        try {
            user.setNickname(request.getNickname());
            userRepository.save(user);
        } catch (Exception e) {
            throw new InternalServerErrorException("닉네임 업데이트 중 에러 발생", e);
        }
        log.info("COMPLETE | 닉네임 업데이트 At " + LocalDateTime.now() + " | " + userId);
        return user.getId();
    }

    /**
     * 임시 비밀번호 생성 |
     * 숫자, 대문자 영문, 소문자 영문의 10가지 조합을 생성해 반환한다.
     */
    private String generateTemporaryPassword() {
        log.info("INITIALIZE | 임시 비밀번호 생성 At " + LocalDateTime.now());
        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        log.info("COMPLETE | 임시 비밀번호 생성 At " + LocalDateTime.now());
        return sb.toString();
    }

    /**
     * 유저 비밀번호 재설정 |
     * 유저를 조회해 현재 비밀번호를 확인한 후 새 비밀번호와 새 비밀번호 재입력을 비교하여 비밀번호 재설정합니다. 존재하지 않은 유저일 경우 404(NotFound)를
     * 던지고, 현재 비밀번호가 틀리면 401(Unauthorized)를 던지고, 새 비밀번호와 새 비밀번호 재입력이 다르면 406(Not Acceptable)을 던지고, 서버
     * 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void resetPassword(UserResetPasswordRequestDto request, String userId) {
        log.info("INITIALIZE | 유저 비밀번호 재설정 At " + LocalDateTime.now() + " | " + userId);
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    if (passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        if (request.getNewPassword().equals(request.getNewPasswordReEntered())) {
                            try {
                                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                                userRepository.save(user);
                            } catch (Exception e) {
                                throw new InternalServerErrorException("유저 비밀번호 재설정 중 에러", e);
                            }
                        } else {
                            throw new NotAcceptableException("새 비밀번호와 새 비밀번호 재입력이 다릅니다");
                        }
                    } else {
                        throw new UnauthorizedException("현재 비밀번호가 틀렸습니다");
                    }
                }, () -> {
                    throw new NotFoundException("존재하지 않는 유저입니다");
                }
        );
        log.info("COMPLETE | 유저 비밀번호 재설정 At " + LocalDateTime.now() + " | " + userId);
    }

    /**
     * 유저 탈퇴 |
     * 유저를 조회해 탈퇴 여부와 비밀번호를 확인하여 탈퇴를 시킨다. 존재하지 않은 유저일 경우 404(NotFound)를 던지고, 비밀번호가 틀리면
     * 401(Unauthorized)를 던지고, 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     * 던진다.
     */
    public void deactivateUser(UserDeactivateRequestDto request, String userId) {
        log.info("INITIALIZE | 유저 탈퇴 At " + LocalDateTime.now() + " | " + userId);
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    if (!user.getIsDeactivated() &&
                            passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        user.setIsDeactivated(true);
                        contactService.deactivateContact(user.getContact());
                        try {
                            userRepository.save(user);
                            log.info("COMPLETE | 유저 탈퇴 At " + LocalDateTime.now() + " | " + userId);
                            return;
                        } catch (Exception e) {
                            throw new InternalServerErrorException("유저 탈퇴 중 에러 발생", e);
                        }
                    }
                    throw new UnauthorizedException("비밀번호가 틀렸습니다");
                }, () -> {
                    throw new NotFoundException("존재하지 않은 유저입니다");
                });
    }

    /**
     * 유저 존재 여부 확인 |
     * 유저 존재 여부를 확인한다. 존재하지 않거나 탈퇴한 유저일 경우 404(Not Found)를 던진다.
     */
    public void isExistingUser(String id) {
        userRepository.findById(id)
                .ifPresentOrElse(user -> {
                    if (user.getIsDeactivated()) {
                        throw new NotFoundException("존재하지 않는 유저입니다");
                    }
                }, () -> {
                    throw new NotFoundException("존재하지 않는 유저입니다");
                });
    }

    /**
     * 프로필 저장 |
     * 유저의 존재 여부를 확인하고 프로필 정보를 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void saveProfile(String id, String profileId) {
        userRepository.findById(id)
                .ifPresent(user -> {
                    user.setProfileId(profileId);
                    try {
                        userRepository.save(user);
                    } catch (Exception e) {
                        throw new InternalServerErrorException("프로필 저장 중 에러 발생", e);
                    }
                });
    }

    /**
     * 유저 전체 삭제 |
     * 배포 단계에서 삭제
     */
    public void deleteAll() {
        try {
            log.info("INITIALIZE | 유저 전체 삭제 At " + LocalDateTime.now());
            userRepository.deleteAll();
        } catch (Exception e) {
            throw new InternalServerErrorException("유저 전체 삭제 중 에러", e);
        }
        log.info("COMPLETE | 유저 전체 삭제 At " + LocalDateTime.now());
    }
}
