package com.gabojait.gabojaitspring.api.service.user;

import com.gabojait.gabojaitspring.api.dto.user.request.UserFindPasswordRequest;
import com.gabojait.gabojaitspring.api.dto.user.request.UserLoginRequest;
import com.gabojait.gabojaitspring.api.dto.user.request.UserRegisterRequest;
import com.gabojait.gabojaitspring.api.dto.user.response.UserDefaultResponse;
import com.gabojait.gabojaitspring.common.util.EmailUtility;
import com.gabojait.gabojaitspring.common.util.PasswordUtility;
import com.gabojait.gabojaitspring.domain.notification.Fcm;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Role;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.domain.user.UserRole;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.notification.FcmRepository;
import com.gabojait.gabojaitspring.repository.notification.NotificationRepository;
import com.gabojait.gabojaitspring.repository.profile.EducationRepository;
import com.gabojait.gabojaitspring.repository.profile.PortfolioRepository;
import com.gabojait.gabojaitspring.repository.profile.SkillRepository;
import com.gabojait.gabojaitspring.repository.profile.WorkRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import com.gabojait.gabojaitspring.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final UserRoleRepository userRoleRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final FcmRepository fcmRepository;
    private final NotificationRepository notificationRepository;
    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillRepository skillRepository;
    private final WorkRepository workRepository;
    private final PasswordUtility passwordUtility;
    private final EmailUtility emailUtility;

    /**
     * 아이디 검증 |
     * 409(UNAVAILABLE_USERNAME / EXISTING_USERNAME)
     * @param username 아이디
     */
    public void validateUsername(String username) {
        if (username.toLowerCase().contains("gabojait") || username.toLowerCase().contains("admin"))
            throw new CustomException(UNAVAILABLE_USERNAME);

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent())
            throw new CustomException(EXISTING_USERNAME);
    }

    /**
     * 닉네임 검증 |
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     * @param nickname 닉네임
     */
    public void validateNickname(String nickname) {
        if (nickname.contains("가보자잇"))
            throw new CustomException(UNAVAILABLE_NICKNAME);

        Optional<User> user = userRepository.findByNickname(nickname);

        if (user.isPresent())
            throw new CustomException(EXISTING_NICKNAME);
    }

    /**
     * 회원 가입 |
     * 400(PASSWORD_MATCH_INVALID / VERIFICATION_CODE_INVALID)
     * 404(EMAIL_NOT_FOUND)
     * 409(UNAVAILABLE_USERNAME / EXISTING_USERNAME / UNAVAILABLE_NICKNAME / EXISTING_NICKNAME / EXISTING_CONTACT)
     * @param request 회원 가입 요청
     * @param lastRequestAt 마지막 요청일
     * @return 회원 기본 응답
     */
    @Transactional
    public UserDefaultResponse register(UserRegisterRequest request, LocalDateTime lastRequestAt) {
        validateUsername(request.getUsername());
        validateNickname(request.getNickname());
        validatePassword(request.getPassword(), request.getPasswordReEntered());
        Contact contact = findAndValidateContact(request.getEmail(), request.getVerificationCode());

        String password = passwordUtility.encodePassword(request.getPassword());

        User user = request.toEntity(password, contact, lastRequestAt);
        userRepository.save(user);
        createUserRole(user, Role.USER);
        createFcm(user, request.getFcmToken());

        return new UserDefaultResponse(user);
    }

    /**
     * 회원 로그인 |
     * 401(LOGIN_UNAUTHENTICATED)
     * 404(USER_NOT_FOUND)
     * @param request 회원 로그인 요청
     * @param lastRequestAt 마지막 요청일
     * @return 회원 기본 응답
     */
    @Transactional
    public UserDefaultResponse login(UserLoginRequest request, LocalDateTime lastRequestAt) {
        User user = findUser(request.getUsername());

        boolean isValid = passwordUtility.verifyPassword(user, request.getPassword());

        if (!isValid)
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        user.updateLastRequestAt(lastRequestAt);
        createFcm(user, request.getFcmToken());

        return new UserDefaultResponse(user);
    }

    /**
     * 회원 로그아웃 |
     * 404(USER_NOT_FOUND)
     * @param username 아이디
     * @param fcmToken FCM 토큰
     */
    @Transactional
    public void logout(String username, String fcmToken) {
        User user = findUser(username);

        findFcm(user, fcmToken).ifPresent(fcmRepository::delete);
    }

    /**
     * 회원 권한들 생성
     * @param user 회원
     * @param role 권한
     */
    @Transactional
    public void createUserRole(User user, Role role) {
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);
    }

    /**
     * FCM 생성
     * @param user 회원
     * @param fcmToken FCM 토큰
     */
    @Transactional
    public void createFcm(User user, String fcmToken) {
        if (fcmToken == null || fcmToken.isBlank())
            return;

        Optional<Fcm> fcm = fcmRepository.findByUserAndFcmToken(user, fcmToken);
        if (fcm.isPresent())
            return;

        Fcm newFcm = Fcm.builder()
                .user(user)
                .fcmToken(fcmToken)
                .build();
        fcmRepository.save(newFcm);
    }

    /**
     * 회원 정보 조회 |
     * 404(USER_NOT_FOUND)
     * @param username 아이디
     * @return 회원 기본 응답
     */
    public UserDefaultResponse findUserInfo(String username) {
        User user = findUser(username);

        return new UserDefaultResponse(user);
    }

    /**
     * FCM 토큰 업데이트 |
     * 404(USER_NOT_FOUND)
     * @param username 아이디
     * @param fcmToken FCM 토큰
     */
    @Transactional
    public void updateFcmToken(String username, String fcmToken, LocalDateTime lastRequestAt) {
        User user = findUser(username);

        user.updateLastRequestAt(lastRequestAt);
        createFcm(user, fcmToken);
    }

    /**
     * 아이디를 이메일로 전송 |
     * 404(CONTACT_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR)
     * @param email 이메일
     */
    public void sendUsernameToEmail(String email) {
        User user = findAndValidateRegisteredUser(email);

        emailUtility.sendEmail(
                user.getContact().getEmail(),
                "[가보자IT] 아이디 찾기",
                "해당 이메일로 가입된 아이디 정보입니다.",
                user.getUsername()
        );
    }

    /**
     * 비밀번호 이메일로 전송 |
     * 400(USERNAME_EMAIL_MATCH_INVALID)
     * 404(USER_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR)
     * @param request 회원 비밀번호 찾기 요청
     */
    @Transactional
    public void sendPasswordToEmail(UserFindPasswordRequest request) {
        User user = findUser(request.getUsername());

        if (!user.getContact().getEmail().equals(request.getEmail()))
            throw new CustomException(USERNAME_EMAIL_MATCH_INVALID);

        String tempPassword = passwordUtility.generateRandomCode(8);
        user.updatePassword(tempPassword, true);

        emailUtility.sendEmail(
                user.getContact().getEmail(),
                "[가보자IT] 비밀번호 찾기",
                user.getUsername() + "님 안녕하세요!🙇🏻<br>임시 비밀번호를 제공해 드립니다.<br>접속 후 비밀번호를 변경 해주세요.",
                tempPassword
        );
    }

    /**
     * 비밀번호 검증 |
     * 401(PASSWORD_UNAUTHENTICATED)
     * 404(USER_NOT_FOUND)
     * @param username 아이디
     * @param password 비밀번호
     */
    public void verifyPassword(String username, String password) {
        User user = findUser(username);

        boolean isVerified = passwordUtility.verifyPassword(user, password);

        if (!isVerified)
            throw new CustomException(PASSWORD_UNAUTHENTICATED);
    }

    /**
     * 닉네임 업데이트 |
     * 404(USER_NOT_FOUND)
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     * @param username 아이디
     * @param nickname 닉네임
     */
    @Transactional
    public void updateNickname(String username, String nickname) {
        User user = findUser(username);

        validateNickname(nickname);

        user.updateNickname(nickname);
    }

    /**
     * 비밀번호 업데이트 |
     * 400(PASSWORD_MATCH_INVALID)
     * 404(USER_NOT_FOUND)
     * @param username 아이디
     * @param password 비밀번호
     * @param passwordReEntered 비밀번호 재입력
     */
    @Transactional
    public void updatePassword(String username, String password, String passwordReEntered) {
        User user = findUser(username);

        validatePassword(password, passwordReEntered);

        String encodedPassword = passwordUtility.encodePassword(password);
        user.updatePassword(encodedPassword, false);
    }

    /**
     * 알림 여부 업데이트 |
     * 404(USER_NOT_FOUND)
     * @param username 아이디
     * @param isNotified 알림 여부
     */
    @Transactional
    public void updateIsNotified(String username, boolean isNotified) {
        User user = findUser(username);

        user.updateIsNotified(isNotified);
    }

    /**
     * 회원 탈퇴 |
     * 404(USER_NOT_FOUND)
     * 409(UNREGISTER_UNAVAILABLE)
     * @param username 아이디
     */
    @Transactional
    public void deleteAccount(String username) {
        User user = findUser(username);

        fcmRepository.deleteAll(fcmRepository.findAllByUser(user));
        notificationRepository.deleteAll(notificationRepository.findAllByUser(user));
        userRoleRepository.deleteAll(userRoleRepository.findAll(username));

        educationRepository.deleteAll(educationRepository.findAll(user.getId()));
        portfolioRepository.deleteAll(portfolioRepository.findAll(user.getId()));
        skillRepository.deleteAll(skillRepository.findAll(user.getId()));
        workRepository.deleteAll(workRepository.findAll(user.getId()));

        teamMemberRepository.findAll(user.getId()).forEach(TeamMember::disconnectUser);

        userRepository.delete(user);
    }

    /**
     * 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     * @param username 아이디
     * @return 회원
     */
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 가입된 회원 단건 조회 및 검증 |
     * 404(CONTACT_NOT_FOUND)
     * @param email 이메일
     * @return 회원
     */
    private User findAndValidateRegisteredUser(String email) {
        Contact contact = contactRepository.findByEmailAndIsVerified(email, true)
                .orElseThrow(() -> {
                    throw new CustomException(CONTACT_NOT_FOUND);
                });

        return userRepository.findByContact(contact)
                .orElseThrow(() -> {
                    throw new CustomException(CONTACT_NOT_FOUND);
                });
    }

    /**
     * 인증 연락처 단건 조회 및 검증 |
     * 400(VERIFICATION_CODE_INVALID)
     * 404(EMAIL_NOT_FOUND)
     * 409(EXISTING_CONTACT)
     * @param email 이메일
     * @param verificationCode 인증코드
     * @return 연락처
     */
    private Contact findAndValidateContact(String email, String verificationCode) {
        Optional<Contact> contact = contactRepository.findByEmailAndIsVerified(email, true);

        if (contact.isEmpty())
            throw new CustomException(EMAIL_NOT_FOUND);

        Optional<User> user = userRepository.findByContact(contact.get());
        if (user.isPresent())
            throw new CustomException(EXISTING_CONTACT);

        if (!verificationCode.equals(contact.get().getVerificationCode()))
            throw new CustomException(VERIFICATION_CODE_INVALID);

        return contact.get();
    }

    /**
     * FCM 토큰 단건 조회 |
     * @param user 회원
     * @param fcmToken FCM 토큰
     * @return FCM
     */
    private Optional<Fcm> findFcm(User user, String fcmToken) {
        return fcmRepository.findByUserAndFcmToken(user, fcmToken);
    }

    /**
     * 비밀번호와 비밀번호 재입력 검증 |
     * 400(PASSWORD_MATCH_INVALID)
     * @param password 비밀번호
     * @param passwordReEntered 비밀번호 재입력
     */
    private void validatePassword(String password, String passwordReEntered) {
        if (!password.equals(passwordReEntered))
            throw new CustomException(PASSWORD_MATCH_INVALID);
    }
}
