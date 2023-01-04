package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.Gender;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.dto.UserLoginRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.UserSaveRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.UserUpdatePasswordRequestDto;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ContactService contactService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 중복 아이디 여부 확인 |
     * 중복 아이디 존재를 파악하고, 이미 사용중인 아이디면 400(BAD_REQUEST)를 던진다.
     */
    public void isExistingUsername(String username) {
        log.info("INITIALIZE | UserService | isExistingUsername | " + username);
        LocalDateTime initTime = LocalDateTime.now();

        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    if (!user.getIsDeleted()) {
                        throw new CustomException(EXISTING_USERNAME);
                    }
                });

        log.info("COMPLETE | UserService | isExistingUsername | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + username);
    }

    /**
     * 중복 닉네임 여부 확인 |
     * 중복 닉네임 존재를 파악하고, 이미 사용중인 닉네임이면 400(BAD_REQUEST)를 던진다.
     */
    public void isExistingNickname(String nickname) {
        log.info("INITIALIZE | UserService | isExistingNickname | " + nickname);
        LocalDateTime initTime = LocalDateTime.now();

        userRepository.findByNickname(nickname)
                .ifPresent(user -> {
                    if (!user.getIsDeleted()) {
                        throw new CustomException(EXISTING_NICKNAME);
                    }
                });

        log.info("COMPLETE | UserService | isExistingNickname | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + nickname);
    }

    /**
     * 회원 가입 |
     * 회원 가입 절차를 밟아서 정보를 저장한다. 서버 에러가 발생하면 500(INTERNAL_SERVER_ERROR)을 던진다.
     */
    public String save(UserSaveRequestDto request) {
        log.info("INITIALIZE | UserService | save | " + request.getUsername());
        LocalDateTime initTime = LocalDateTime.now();

        Contact contact = contactService.findOneByEmail(request.getEmail());
        contactService.register(contact);

        Gender gender = validateGender(request.getGender());
        String password = validatePassword(request.getPassword(), request.getPasswordReEntered());

        User user = request.toEntity(password, gender, contact);

        try {
            userRepository.save(user);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | UserService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return user.getId().toString();
    }

    /**
     * 비밀번호 검증 |
     * 비밀번호와 비밀번호 재입력이 동일한지 확인하고 암호화된 비밀번호를 반환한다. 두 비밀번호가 동일하지 않은 경우 400(BAD_REQUEST)를 던진다.
     */
    private String validatePassword(String password, String passwordReEntered) {
        if (!password.equals(passwordReEntered)) {
            throw new CustomException(PASSWORD_VALIDATION_FAIL);
        }

        log.info("PROGRESS | UserService | validatePassword");
        return passwordEncoder.encode(password);
    }

    /**
     * 성별 검증 |
     * 성별이 남자 'M' 또는 여자 'F'로 되어 있는지 확인한다. 올바르지 않을 포맷일 경우 400(BAD_REQUEST)을 던진다.
     */
    private Gender validateGender(Character gender) {
        log.info("PROGRESS | UserService | validateGender | " + gender);

        if (gender == Gender.MALE.getType()) {
            return Gender.MALE;
        } else if (gender == Gender.FEMALE.getType()) {
            return Gender.FEMALE;
        } else {
            throw new CustomException(INCORRECT_GENDER);
        }
    }

    /**
     * JWT 토큰 생성 |
     * JWT 토큰을 생성 또는 재생성한다.
     */
    public HttpHeaders generateJwtToken(User user) {
        log.info("PROGRESS | UserService | generateJwtToken | " + user.getUsername());

        String[] tokens = jwtProvider.generateJwt(user);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("ACCESS-TOKEN", tokens[0]);
        responseHeader.add("REFRESH-TOKEN", tokens[1]);

        return responseHeader;
    }

    /**
     * 회원 단건 조회 |
     * 회원 정보를 조회하여 반환합니다. 존재하지 않은 유저아이디 일 경우 404(NOT_FOUND)를 던진다.
     */
    public User findOne(String userId) {
        log.info("INITIALIZE | UserService | findOne | " + userId);
        LocalDateTime initTime = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new CustomException(NON_EXISTING_USER);
                });

        log.info("COMPLETE | UserService | findOne | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return user;
    }

    /**
     * 로그인 |
     * 아이디와 비밀번호를 통해 로그인을 진행한다. 아이디 또는 비밀번호가 틀렸을 경우 401(UNAUTHORIZED)를 던진다.
     */
    public User login(UserLoginRequestDto request) {
        log.info("INITIALIZE | UserService | login | " + request.getUsername());
        LocalDateTime initTime = LocalDateTime.now();

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    throw new CustomException(LOGIN_FAIL);
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(LOGIN_FAIL);
        }

        log.info("COMPLETE | UserService | login | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return user;
    }

    /**
     * 아이디로 회원 단건 조회 |
     * 아이디로 회원 정보를 조회하여 반환한다. 존재하지 않은 아이디일 경우 404(NOT_FOUND)를 던진다.
     */
    public User findOneByUsername(String username) {
        log.info("INITIALIZE | UserService | findOneByUsername | " + username);
        LocalDateTime initTime = LocalDateTime.now();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new CustomException(NON_EXISTING_USER);
                });

        log.info("COMPLETE | UserService | findOneByUsername | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return user;
    }

    /**
     * 아이디 찾기 |
     * 이메일로 유저를 조회하여 해당 이메일로 아이디를 보낸다. 조회가 되지 않거나 탈퇴한 유저일 경우 404(NOT_FOUND)를 던진다.
     */
    public void findForgotUsername(String email) {
        log.info("INITIALIZE | UseService | findForgotUsernameByEmail | " + email);
        LocalDateTime initTime = LocalDateTime.now();

        User user = userRepository.findByContact(email)
                .orElseThrow(() -> {
                    throw new CustomException(NON_EXISTING_USER);
                });

        if (user.getIsDeleted()) {
            throw new CustomException(NON_EXISTING_USER);
        } else {
            emailService.sendEmail(
                    user.getContact().getEmail(),
                    "[가보자잇] 아이디 찾기",
                    user.getLegalName() + "님 안녕하세요!🙇🏻<br>해당 이메일로 가입된 아이디 정보입니다.",
                    user.getUsername()
            );
        }

        log.info("COMPLETE | UserService | findForgotUsernameByEmail | " +
                Duration.between(initTime, LocalDateTime.now()) + " | " + user.getUsername());
    }

    /**
     * 아이디와 이메일로 비밀번호 초기화 |
     * 아이디로 유저를 조회 후 비밀번호를 초기화하여 이메일로 초기화된 비밀번호를 보낸다. 존재하지 않거나 탈퇴한 유저인 경우 404(NOT_FOUND)를 던진다. 서버
     * 에러가 발생하면 500(INTERNAL_SERVER_ERROR)을 던진다.
     */
    public void resetForgotPassword(String username, String email) {
        log.info("INITIALIZE | UseService | findForgotUsernameByEmail | " + username + " | " + email);
        LocalDateTime initTime = LocalDateTime.now();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new CustomException(NON_EXISTING_USER);
                });

        if (user.getIsDeleted()) {
            throw new CustomException(NON_EXISTING_USER);
        }

        if (email.equals(user.getContact().getEmail())) {
            String tempPassword = generateTemporaryPassword();
            user.setPassword(passwordEncoder.encode(tempPassword));

            emailService.sendEmail(
                    user.getContact().getEmail(),
                    "[가보자잇] 비밀번호 찾기",
                    user.getLegalName() +
                            "님 안녕하세요!🙇🏻<br>임시 비밀번호를 제공해 드립니다.<br>접속 후 비밀번호를 변경 해주세요.",
                    tempPassword
            );

            try {
                userRepository.save(user);
            } catch (RuntimeException e) {
                throw new CustomException(SERVER_ERROR);
            }
        } else {
            throw new CustomException(NON_EXISTING_EMAIL);
        }

        log.info("COMPLETE | UseService | findForgotUsernameByEmail | " + username);
    }

    /**
     * 임시 비밀번호 생성 |
     * 알파벳 대문자와 소문자와 숫자를 조합하여 임시 비밀번호를 생성한다.
     */
    private String generateTemporaryPassword() {
        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));

        log.info("PROGRESS | UseService | generateTemporaryPassword" + sb);
        return sb.toString();
    }

    /**
     * 비밀번호 업데이트 |
     * 현재 비밀번호가 동일한지 확인 후 새 비밀번호와 새 비밀번호 재입력이 동일한지 확인하고 비밀번호 업데이트 한다. 탈퇴한 회원이거나 현재 비밀번호가 동일하지
     * 않으면 401(UNAUTHORIZED)을 던진다. 새 배밀번호와 새 배밀번호 재입력이 동일하지 않으면 400(BAD_REQUEST)를 던진다. 서버 에러가 발생하면
     * 500(INTERNAL_SERVER_ERROR)을 던진다.
     */
    public User updatePassword(User user, UserUpdatePasswordRequestDto request) {
        log.info("INITIALIZE | UseService | updatePassword | " + user.getUsername());
        LocalDateTime initTime = LocalDateTime.now();

        if (!user.getIsDeleted() && passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            if (request.getNewPassword().equals(request.getNewPasswordReEntered())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            } else {
                throw new CustomException(PASSWORD_VALIDATION_FAIL);
            }
        } else {
            throw new CustomException(INCORRECT_PASSWORD);
        }

        try {
            user = userRepository.save(user);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | UserService | updatePassword | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return user;
    }

    /**
     * 닉네임 업데이트 |
     * 닉네임을 업데이트 시킨다. 탈퇴한 유저인 경우 404(NOT_FOUND)을 던지고 서버 에러가 발생하면 500(INTERNAL_SERVER_ERROR)을 던진다.
     */
    public User updateNickname(User user, String nickname) {
        log.info("INITIALIZE | UserService | updateNickname | " + user.getUsername() + " | " + user.getNickname());
        LocalDateTime initTime = LocalDateTime.now();

        if (user.getIsDeleted()) {
            throw new CustomException(NON_EXISTING_USER);
        }

        user.setNickname(nickname);

        try {
            user = userRepository.save(user);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | UserService | updateNickname | " + Duration.between(initTime, LocalDateTime.now()) + " | "
                + user.getUsername() + " | " + user.getNickname());
        return user;
    }

    /**
     * 회원 탈퇴 |
     * 모든 회원 관련 정보에 탈퇴 여부를 true 로 바꾼다. 서버 에러가 발생하면 500(INTERNAL_SERVER_ERROR)을 던진다.
     */
    public void deactivate(User user) {
        log.info("INITIALIZE | UseService | deactivate | " + user.getUsername());
        LocalDateTime initTime = LocalDateTime.now();

        user.setIsDeleted(true);
        user.getContact().setIsDeleted(true);

        try {
            user = userRepository.save(user);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | UserService | deactivate | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
    }

    /**
     * 전체 삭제 |
     * 모든 회원을 삭제한다. 서버 에러가 발생하면 500(INTERNAL_SERVER_ERROR)을 던진다.
     * TODO: 배포 전 삭제 필요
     */
    public void deleteAll() {
        log.info("INITIALIZE | UseService | deleteAll");
        LocalDateTime initTime = LocalDateTime.now();

        try {
            userRepository.deleteAll();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | UserService | deleteAll | " + Duration.between(initTime, LocalDateTime.now()));
    }
}
