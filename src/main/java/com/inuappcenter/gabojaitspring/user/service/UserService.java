package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.http.*;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.dto.*;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ContactService contactService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final String tokenPrefix = "Bearer ";


    /**
     * 중복 유저이름 존재 여부 확인 |
     * 중복 유저이름 존재를 파악하고, 이미 사용중인 유저이름이면 409(Conflict)를 던진다.
     */
    public void isExistingUsername(String username) {
        log.info("INITIALIZE | UserService | isExistingUsername | " + username);
        LocalDateTime initTime = LocalDateTime.now();

        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    if (!user.getIsDeactivated()) {
                        throw new ConflictException("이미 사용중인 아이디입니다");
                    }
                });

        log.info("COMPLETE | UserService | isExistingUsername | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + username);
    }

    /**
     * 중복 닉네임 존재 여부 확인 |
     * 중복 닉네임 존재를 파악하고, 이미 사용중인 닉네임이면 409(Conflict)를 던진다.
     */
    public void isExistingNickname(String nickname) {
        log.info("INITIALIZE | UserService | isExistingNickname | " + nickname);
        LocalDateTime initTime = LocalDateTime.now();

        userRepository.findByNickname(nickname)
                .ifPresent(user -> {
                    if (!user.getIsDeactivated()) {
                        throw new ConflictException("이미 사용중인 닉네임입니다");
                    }
                });

        log. info("COMPLETE | UserService | isExistingNickname | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + nickname);
    }

    /**
     * 유저 저장 |
     * 유저의 정보를 저장한다. 해당 유저가 이메일 인증을 하지 않았을 경우 401(Unauthorized)를 던지고, 서버 에러가 발생하면
     * 500(Internal Server Error)을 던진다.
     */
    public void save(UserSaveRequestDto request) {
        log.info("INITIALIZE | UserService | save | " + request.getUsername());
        LocalDateTime initTime = LocalDateTime.now();

        Contact contact = contactService.findOneContact(request.getEmail());
        if (!contact.getIsVerified()) {
            throw new NotFoundException("이메일 인증을 해주세요");
        }

        isExistingUsername(request.getUsername());
        isExistingNickname(request.getNickname());

        contactService.register(contact);
        User user = assignAsUser(request.toEntity(contact), "USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            user = userRepository.save(user);

            log.info("COMPLETE | UserService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                    user.getUsername());
        } catch (Exception e) {
            throw new InternalServerErrorException("유저 저장 중 에러", e);
        }
    }

    /**
     * 유저 역할 부여 |
     * 유저에게 "USER" 또는 "ADMIN" 역할을 부여한다.
     */
    public User assignAsUser(User user, String role) {
        log.info("INITIALIZE | UserService | assignAsUser | " + user.getUsername() + " | " + role);
        LocalDateTime initTime = LocalDateTime.now();

        user.addRole(role);

        log.info("COMPLETE | UserService | assignAsUser | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername() + " | " + user.getRoles());
        return user;
    }

    /**
     * JWT로 유저 단건 조회 |
     * JWT로 유저 단건 조회를 한다. 조회가 되지 않거나 탈퇴한 유저일 경우 404(NotFound)를 던지고, 서버 에러가 발생하면
     * 500(Internal Server Error)을 던진다.
     */
    public UserDefaultResponseDto findOneUserByToken(String token) {
        log.info("INITIALIZE | UserService | findOneUser | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        String username = jwtProvider.loadUsernameByJwt(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new InternalServerErrorException("유저 조회 중 에러");
                });

        if (user.getIsDeactivated()) {
            throw new UsernameNotFoundException("회원 탈퇴한 유저입니다");
        }

        log.info("COMPLETE | UserService | findOneUser | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return new UserDefaultResponseDto(user);
    }

    /**
     * userId로 유저 단건 조희 |
     * 유저를 조회 하여 유저 엔티티로 반환한다. 조회가 되지 않거나 탈퇴한 유저일 경우 404(NotFound)를 던진다.
     */
    public User findOneUser(String userId) {
        log.info("INITIALIZE | UserService | findOneUser | " + userId);
        LocalDateTime initTime = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("존재하지 않은 유저입니다");
                });

        log.info("COMPLETE | UserService | findOneUser | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return user;
    }

    /**
     * 토큰으로 유저 단건 조회 후 유저 반환 |
     * 토큰으로 유저 단건 조회를 하여 유저를 반환한다. 탈퇴한 유저인 경우 404(Not Found)를 던지고, 서버 에러가 발생하면
     * 500(Internal Server Error)을 던진다.
     */
    public User loadOneUserByToken(String token) {
        log.info("INITIALIZE | UserService | loadOneUserByToken | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        String username = jwtProvider.loadUsernameByJwt(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new InternalServerErrorException("유저 조회 중 에러");
                });

        if (user.getIsDeactivated()) {
            throw new NotFoundException("회원 탈퇴한 유저입니다");
        }

        log.info("COMPLETE | UserService | loadOneUserByToken | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + user.getUsername());
        return user;
    }

    /**
     * 이메일로 아이디 찾기 |
     * 이메일로 유저를 조회하여 해당 이메일로 아이디를 보낸다. 조회가 되지 않거나 탈퇴한 유저일 경우 404(NotFound)를 던진다.
     */
    public void findForgotUsernameByEmail(String email) {
        log.info("INITIALIZE | UserService | findForgotUsernameByEmail | " + email);
        LocalDateTime initTime = LocalDateTime.now();

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

                    log.info("COMPLETE | UserService | findForgotUsernameByEmail | " +
                            Duration.between(initTime, LocalDateTime.now()) + " | " + user.getUsername());
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
        log.info("INITIALIZE | UserService | resetPasswordByEmailAndUsername | " + email + " | " + username);
        LocalDateTime initTime = LocalDateTime.now();

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
                        log.info("COMPLETE | UserService | resetPasswordByEmailAndUsername | "  +
                                Duration.between(initTime, LocalDateTime.now()) + " | " + user.getContact().getEmail() +
                                " | " + user.getUsername());
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
    public void updateNickname(String token, UserUpdateNicknameRequestDto request) {
        log.info("INITIALIZE | UserService | updateNickname | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        User user = loadOneUserByToken(token);
        user.setNickname(request.getNickname());
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new InternalServerErrorException("닉네임 업데이트 중 에러 발생", e);
        }
        log.info("COMPLETE | UserService | updateNickname | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + user.getUsername());
    }

    /**
     * 임시 비밀번호 생성 |
     * 숫자, 대문자 영문, 소문자 영문의 10가지 조합을 생성해 반환한다.
     */
    private String generateTemporaryPassword() {
        log.info("INITIALIZE | UserService | generateTemporaryPassword");
        LocalDateTime initTime = LocalDateTime.now();

        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));

        log.info("COMPLETE | UserService | generateTemporaryPassword | " +
                Duration.between(initTime, LocalDateTime.now()) + " | " + sb);
        return sb.toString();
    }

    /**
     * 유저 비밀번호 재설정 |
     * 유저를 조회해 현재 비밀번호를 확인한 후 새 비밀번호와 새 비밀번호 재입력을 비교하여 비밀번호 재설정합니다. 존재하지 않은 유저일 경우 404(NotFound)를
     * 던지고, 새 비밀번호와 새 비밀번호 재입력이 다르면 406(Not Acceptable)을 던지고, 현재 비밀번호가 틀리면 409(Conflict)를 던지고, 서버 에러가
     * 발생하면 500(Internal Server Error)을 던진다.
     */
    public void resetPassword(String token, UserResetPasswordRequestDto request) {
        log.info("INITIALIZE | UserService | resetPassword | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        User user = loadOneUserByToken(token);

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
            throw new ConflictException("현재 비밀번호가 틀렸습니다");
        }

        log.info("COMPLETE | UserService | resetPassword | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
    }

    /**
     * 유저 탈퇴 |
     * 유저를 조회해 탈퇴 여부와 비밀번호를 확인하여 탈퇴를 시킨다. 존재하지 않은 유저일 경우 404(NotFound)를 던지고, 비밀번호가 틀리면
     * 401(Unauthorized)를 던지고, 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     * 던진다.
     */
    public void deactivateUser(String token, UserDeactivateRequestDto request) {
        log.info("INITIALIZE | UserService | deactivateUser | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        User user = loadOneUserByToken(token);

        if (!user.getIsDeactivated() && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setIsDeactivated(true);
            contactService.deactivateContact(user.getContact());

            try {
                userRepository.save(user);
            } catch (Exception e) {
                throw new InternalServerErrorException("유저 탈퇴 중 에러 발생", e);
            }
        }
        else {
            throw new ConflictException("비밀번호가 틀렸습니다");
        }

        log.info("COMPLETE | UserService | deactivateUser | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
    }

    /**
     * 프로필 저장 |
     * 유저의 존재 여부를 확인하고 프로필 정보를 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void saveProfile(String userId, String profileId) {
        log.info("INITIALIZE | UserService | saveProfile | " + userId);
        LocalDateTime initTime = LocalDateTime.now();

        userRepository.findById(userId)
                .ifPresent(user -> {
                    user.setProfileId(profileId);
                    try {
                        userRepository.save(user);
                    } catch (Exception e) {
                        throw new InternalServerErrorException("프로필 저장 중 에러 발생", e);
                    }
                });

        log.info("COMPLETE | UserService | saveProfile | " + Duration.between(initTime, LocalDateTime.now()) + userId);
    }

    /**
     * 유저 토큰 생성 |
     * 유저 아이디와 비밀번호로 인증을 한 후 토큰을 생성한다. 존재하지 않거나 탈퇴한 유저인 경우 404(Not Found)를 던지고, 비밀번호가 틀렸을 경우
     * 401(Unauthorized)을 던진다.
     */
    public String[] generateToken(String username, String password) {
        log.info("INITIALIZE | UserService | generateToken | " + username);
        LocalDateTime initTime = LocalDateTime.now();

        userRepository.findByUsername(username)
                .ifPresentOrElse(user -> {
                    if (user.getIsDeactivated()) {
                        throw new NotFoundException("탈퇴한 유저입니다");
                    }
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        throw new UnauthorizedException("비밀번호가 틀렸습니다");
                    }
                }, () -> {
                    throw new NotFoundException("존재하지 않는 유저입니다");
                });

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authToken);
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String[] token = jwtProvider.generateJwt(user);

        log.info("COMPLETE | UserService | generateToken | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return token;
    }

    /**
     * 유저 토큰 재생성 |
     * 유저 토큰으로 인증을 한 후 토큰을 재생성한다. 인증에 실패하였을때 401(Unauthorized)을 던진다.
     */
    public String[] regenerateToken(String token) {
        log.info("INITIALIZE | UserService | regenerateToken | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        org.springframework.security.core.userdetails.User user = jwtProvider.verifyJwt(token);
        String[] renewedToken = jwtProvider.generateJwt(user);

        log.info("COMPLETE | UserService | regenerateToken | " + Duration.between(initTime, LocalDateTime.now()) +
                user.getUsername());
        return renewedToken;
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
