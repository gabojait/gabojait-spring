package com.gabojait.gabojaitspring.user.service;

import com.gabojait.gabojaitspring.common.util.EmailProvider;
import com.gabojait.gabojaitspring.common.util.PasswordProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.fcm.domain.Fcm;
import com.gabojait.gabojaitspring.fcm.repository.FcmRepository;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.UserRole;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.dto.req.UserFindPasswordReqDto;
import com.gabojait.gabojaitspring.user.dto.req.UserLoginReqDto;
import com.gabojait.gabojaitspring.user.dto.req.UserRegisterReqDto;
import com.gabojait.gabojaitspring.user.dto.req.UserRenewTokenReqDto;
import com.gabojait.gabojaitspring.user.repository.ContactRepository;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ContactRepository contactRepository;
    private final FcmRepository fcmRepository;
    private final PasswordProvider passwordProvider;
    private final EmailProvider emailProvider;

    /**
     * 아이디 검증 |
     * 409(UNAVAILABLE_USERNAME / EXISTING_USERNAME)
     */
    public void validateUsername(String username) {
        if (username.toLowerCase().contains("admin") || username.toLowerCase().contains("gabojait"))
            throw new CustomException(UNAVAILABLE_USERNAME);

        validateDuplicateUsername(username);
    }

    /**
     * 닉네임 검증 |
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     */
    public void validateNickname(String nickname) {
        if (nickname.toLowerCase().contains("관리자") || nickname.toLowerCase().contains("가보자잇"))
            throw new CustomException(UNAVAILABLE_NICKNAME);

        validateDuplicateNickname(nickname);
    }

    /**
     * 회원 가입 |
     * 400(PASSWORD_MATCH_INVALID)
     * 404(CONTACT_NOT_FOUND)
     * 409(UNAVAILABLE_USERNAME / EXISTING_USERNAME / UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     * 500(SERVER_ERROR)
     */
    public User register(UserRegisterReqDto request) {
        validateUsername(request.getUsername());
        validateNickname(request.getNickname());
        validateMatchingPassword(request.getPassword(), request.getPasswordReEntered());
        Contact contact = findOneVerifiedUnregisteredContact(request.getEmail());
        String password = passwordProvider.encodePassword(request.getPassword());

        User user = request.toEntity(password, contact);
        saveUser(user);

        UserRole userRole = createUserRole(user);
        saveUserRole(userRole);

        if (request.getFcmToken() != null)
            createFcm(request.getFcmToken(), user);

        return user;
    }

    /**
     * 회원 로그인 |
     * 401(LOGIN_UNAUTHENTICATED)
     * 500(SERVER_ERROR)
     */
    public User login(UserLoginReqDto request) {
        User user = findOneUser(request.getUsername());

        boolean isVerified = passwordProvider.verifyPassword(user, request.getPassword());
        if (!isVerified)
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        if (request.getFcmToken() != null)
            createFcm(request.getFcmToken(), user);
        updateLastRequestAt(user);

        return user;
    }

    /**
     * 회원 로그아웃 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void logout(long userId, String fcmToken) {
        User user = findOneUser(userId);
        Optional<Fcm> fcm = findOneFcm(fcmToken, user);

        fcm.ifPresent(this::hardDeleteFcm);
    }

    /**
     * 마지막 요청일 업데이트 |
     * 500(SERVER_ERROR)
     */
    public void updateLastRequestAt(User user) {
        user.updateLastRequestAt();
    }

    /**
     * 아이디 이메일로 전송 |
     * 404(CONTACT_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR / SERVER_ERROR)
     */
    public void sendUsernameToEmail(String email) {
        Contact contact = findOneRegisteredContact(email);
        sendUsernameEmail(contact.getUser());
    }

    /**
     * 비밀번호 이메일로 전송 |
     * 400(USERNAME_EMAIL_MATCH_INVALID)
     * 404(CONTACT_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR)
     */
    public void sendPasswordByEmail(UserFindPasswordReqDto request) {
        Contact contact = findOneRegisteredContact(request.getEmail());
        User user = contact.getUser();

        if (!user.getUsername().equals(request.getUsername()))
            throw new CustomException(USERNAME_EMAIL_MATCH_INVALID);

        String tempPassword = passwordProvider.generateRandomCode(8);
        updatePassword(user.getId(), tempPassword, tempPassword, true);

        emailProvider.sendEmail(
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
     */
    public void verifyPassword(long userId, String password) {
        User user = findOneUser(userId);

        boolean isVerified = passwordProvider.verifyPassword(user, password);

        if (!isVerified)
            throw new CustomException(PASSWORD_UNAUTHENTICATED);
    }

    /**
     * 닉네임 업데이트 |
     * 404(USER_NOT_FOUND)
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     */
    public void updateNickname(long userId, String nickname) {
        User user = findOneUser(userId);

        validateNickname(nickname);

        user.updateNickname(nickname);
    }

    /**
     * 비밀번호 업데이트 |
     * 400(PASSWORD_MATCH_INVALID)
     * 404(USER_NOT_FOUND)
     */
    public void updatePassword(long userId, String password, String passwordReEntered, boolean isTemporaryPassword) {
        User user = findOneUser(userId);

        if (!isTemporaryPassword)
            validateMatchingPassword(password, passwordReEntered);

        String encodedPassword = passwordProvider.encodePassword(password);
        user.updatePassword(encodedPassword, isTemporaryPassword);
    }

    /**
     * 알림 여부 업데이트 |
     * 404(USER_NOT_FOUND)
     */
    public void updateIsNotified(long userId, boolean isNotified) {
        User user = findOneUser(userId);

        user.updateIsNotified(isNotified);
    }

    /**
     * FCM 토큰 업데이트 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public User updateFcmToken(long userId, UserRenewTokenReqDto request) {
        User user = findOneUser(userId);
        updateLastRequestAt(user);

        if (request.getFcmToken() != null)
            createFcm(request.getFcmToken(), user);
        return user;
    }

    /**
     * 회원 탈퇴 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void deleteAccount(long userId) {
        User user = findOneUser(userId);
        List<Fcm> fcms = user.getFcms();

        for(Fcm fcm : fcms)
            hardDeleteFcm(fcm);

        user.getContact().delete();
        user.deleteAccount();
    }

    /**
     * FCM 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    private void hardDeleteFcm(Fcm fcm) {
        try {
            fcmRepository.delete(fcm);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveUser(User user) {
        try {
            userRepository.save(user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * FCM 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveFcm(Fcm fcm) {
        try {
            fcmRepository.save(fcm);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원 권한 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveUserRole(UserRole userRole) {
        try {
            userRoleRepository.save(userRole);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    public User findOneUser(long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 아이디로 회원 단건 조회 |
     * 401(LOGIN_UNAUTHENTICATED)
     * 500(SERVER_ERROR)
     */
    private User findOneUser(String username) {
        Optional<User> user = userRepository.findByUsernameAndIsDeletedIsFalse(username);

        if (user.isEmpty())
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        Optional<UserRole> userRole = findOneUserRole(user.get());

        if (userRole.isEmpty())
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        return user.get();
    }

    /**
     * 회원과 권한으로 회원 권한 단건 조회 |
     * 500(SERVER_ERROR)
     */
    private Optional<UserRole> findOneUserRole(User user) {
        try {
            return userRoleRepository.findByUserAndRole(user, Role.USER);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 인증되고 가입되지 않은 연락처 단건 조회 |
     * 404(CONTACT_NOT_FOUND)
     */
    private Contact findOneVerifiedUnregisteredContact(String email) {
        Optional<Contact> contact = contactRepository.findByEmailAndIsVerifiedIsTrueAndIsDeletedIsFalse(email);

        if (contact.isEmpty())
            throw new CustomException(CONTACT_NOT_FOUND);
        else if (contact.get().getUser() != null)
            throw new CustomException(CONTACT_NOT_FOUND);

        return contact.get();
    }

    /**
     * 이메일로 가입된 연락처 단건 조회 | main |
     * 404(CONTACT_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    private Contact findOneRegisteredContact(String email) {
        Optional<Contact> contact = contactRepository.findByEmailAndIsVerifiedIsTrueAndIsDeletedIsFalse(email);

        if (contact.isEmpty() || contact.get().getUser() == null)
            throw new CustomException(CONTACT_NOT_FOUND);

        return contact.get();
    }

    /**
     * FCM 토큰과 회원으로 FCM 단건 조회 |
     * 500(SERVER_ERROR)
     */
    private Optional<Fcm> findOneFcm(String fcmToken, User user) {
        try {
            return fcmRepository.findByFcmTokenAndUserAndIsDeletedIsFalse(fcmToken, user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 아이디 이메일로 전송 |
     * 500(EMAIL_SEND_ERROR)
     */
    private void sendUsernameEmail(User user) {
        emailProvider.sendEmail(
                user.getContact().getEmail(),
                "[가보자IT] 아이디 찾기",
                "해당 이메일로 가입된 아이디 정보입니다.",
                user.getUsername()
        );
    }

    /**
     * FCM 생성 |
     * 500(SERVER_ERROR)
     */
    private void createFcm(String fcmToken, User user) {
        if (fcmToken.isBlank())
            return;

        Optional<Fcm> fcm = findOneFcm(fcmToken, user);

        if (fcm.isPresent())
            return;

        Fcm newFcm = Fcm.builder()
                .fcmToken(fcmToken)
                .user(user)
                .build();

        saveFcm(newFcm);
    }

    /**
     * 회원 권한 생성
     */
    private UserRole createUserRole(User user) {
        return UserRole.builder()
                .user(user)
                .role(Role.USER)
                .build();
    }

    /**
     * 중복 아이디 여부 검증 |
     * 409(EXISTING_USERNAME)
     * 500(SERVER_ERROR)
     */
    private void validateDuplicateUsername(String username) {
        Optional<User> user = userRepository.findByUsernameAndIsDeletedIsFalse(username);

        if (user.isPresent()) {
            Optional<UserRole> userRole = findOneUserRole(user.get());

            if (userRole.isPresent())
                throw new CustomException(EXISTING_USERNAME);
        }
    }

    /**
     * 중복 닉네임 여부 검증 |
     * 409(EXISTING_NICKNAME)
     */
    private void validateDuplicateNickname(String nickname) {
        userRepository.findByNicknameAndIsDeletedIsFalse(nickname)
                .ifPresent(u -> {
                    throw new CustomException(EXISTING_NICKNAME);
                });
    }

    /**
     * 비밀번호와 비밀번호 재입력 검증 |
     * 400(PASSWORD_MATCH_INVALID)
     */
    private void validateMatchingPassword(String password, String passwordReEntered) {
        if (!password.equals(passwordReEntered))
            throw new CustomException(PASSWORD_MATCH_INVALID);
    }
}
