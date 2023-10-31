package com.gabojait.gabojaitspring.api.service.user;

import com.gabojait.gabojaitspring.api.dto.user.request.UserFindPasswordRequest;
import com.gabojait.gabojaitspring.api.dto.user.request.UserLoginRequest;
import com.gabojait.gabojaitspring.api.dto.user.request.UserRegisterRequest;
import com.gabojait.gabojaitspring.api.dto.user.response.UserDefaultResponse;
import com.gabojait.gabojaitspring.common.util.PasswordUtility;
import com.gabojait.gabojaitspring.domain.notification.Fcm;
import com.gabojait.gabojaitspring.domain.profile.*;
import com.gabojait.gabojaitspring.domain.user.*;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private FcmRepository fcmRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private EducationRepository educationRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private WorkRepository workRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;
    @Autowired private PasswordUtility passwordUtility;

    @Test
    @DisplayName("아이디 검증을 한다.")
    void givenValid_whenValidateUsername_thenReturn() {
        // given
        String username = "tester";

        // when
        userService.validateUsername(username);

        // then
        Optional<User> user = userRepository.findByUsername(username);

        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("'gabojait'이 포함된 아이디 검증시 예외가 발생한다.")
    void givenGabojaitUsername_whenValidateUsername_thenThrow() {
        // given
        String username = "gabojaittest";

        // when & then
        assertThatThrownBy(() -> userService.validateUsername(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(UNAVAILABLE_USERNAME);
    }

    @Test
    @DisplayName("'admin'이 포함된 아이디 검증시 예외가 발생한다.")
    void givenAdminUsername_whenValidateUsername_thenThrow() {
        // given
        String username = "admintest";

        // when & then
        assertThatThrownBy(() -> userService.validateUsername(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(UNAVAILABLE_USERNAME);
    }

    @Test
    @DisplayName("사용중인 아이디 검증시 예외가 발생한다.")
    void givenExistingUsername_whenValidateUsername_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when & then
        assertThatThrownBy(() -> userService.validateUsername(user.getUsername()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EXISTING_USERNAME);
    }

    @Test
    @DisplayName("닉네임을 검증한다.")
    void givenValid_whenValidateNickname_thenReturn() {
        // given
        String nickname = "테스터";

        // when
        userService.validateNickname(nickname);

        // then
        Optional<User> user = userRepository.findByNickname(nickname);

        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("'가보자잇'이 포함된 닉네임 검증시 예외가 발생한다.")
    void givenGabojaitNickname_whenValidateNickname_thenThrow() {
        // given
        String nickname = "가보자잇크루";

        // when & then
        assertThatThrownBy(() -> userService.validateNickname(nickname))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(UNAVAILABLE_NICKNAME);
    }

    @Test
    @DisplayName("사용중인 닉네임 검증시 예외가 발생한다.")
    void givenExistingNickname_whenValidateNickname_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when & then
        assertThatThrownBy(() -> userService.validateNickname(user.getNickname()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EXISTING_NICKNAME);
    }

    @Test
    @DisplayName("회원 가입을 한다.")
    void givenValid_whenRegister_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        UserRegisterRequest request = createValidUserRegisterRequest();
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when
        UserDefaultResponse response = userService.register(request, lastRequestAt);

        // then
        assertThat(response)
                .extracting("username", "nickname", "gender", "birthdate", "isNotified")
                .containsExactly(request.getUsername(), request.getNickname(), Gender.valueOf(request.getGender()),
                        request.getBirthdate(), true);

        Optional<User> user = userRepository.findByContact(contact);

        List<UserRole> userRoles = userRoleRepository.findAll(user.get().getUsername());
        assertEquals(Role.USER, userRoles.get(0).getRole());

        List<Fcm> fcms = fcmRepository.findAllByUser(user.get());
        assertEquals(request.getFcmToken(), fcms.get(0).getFcmToken());
    }

    @Test
    @DisplayName("동일한하지 않은 비밀번호와 비밀번호 재입력으로 회원가입시 예외가 발생한다.")
    void givenUnMatchingPassword_whenRegister_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setPasswordReEntered("password2!");
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> userService.register(request, lastRequestAt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PASSWORD_MATCH_INVALID);
    }

    @Test
    @DisplayName("틀린 인증번호로 회원가입시 예외가 발생한다.")
    void givenIncorrectVerificationCode_whenRegister_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setVerificationCode("000001");
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> userService.register(request, lastRequestAt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(VERIFICATION_CODE_INVALID);
    }

    @Test
    @DisplayName("연락처 인증요청하지 않은 연락처로 회원가입시 예외가 발생한다.")
    void givenUnverifiedContact_whenRegister_thenThrow() {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> userService.register(request, lastRequestAt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EMAIL_NOT_FOUND);
    }

    @Test
    @DisplayName("회원가입된 연락처로 회원가입시 예외가 발생한다.")
    void givenRegisteredContact_whenRegister_thenThrow() {
        // given
        Contact contact1 = createContact("tester@gabojait.com");
        contact1.verified();
        contactRepository.save(contact1);
        User user = createUser("tester1", "테스터일", contact1);
        userRepository.save(user);

        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setUsername("tester2");
        request.setNickname("테스터이");
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> userService.register(request, lastRequestAt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EXISTING_CONTACT);
    }

    @Test
    @DisplayName("로그인을 한다.")
    void givenValid_whenLogin_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        UserLoginRequest request = createValidUserLoginRequest();
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when
        UserDefaultResponse response = userService.login(request, lastRequestAt);

        // then
        assertEquals(request.getUsername(), response.getUsername());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인시 예외가 발생한다.")
    void givenUnregisteredUser_whenLogin_thenThrow() {
        // given
        UserLoginRequest request = createValidUserLoginRequest();
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> userService.login(request, lastRequestAt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인시 예외가 발생한다.")
    void givenIncorrectPassword_whenLogin_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        UserLoginRequest request = createValidUserLoginRequest();
        request.setPassword("password2!");
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> userService.login(request, lastRequestAt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(LOGIN_UNAUTHENTICATED);
    }

    @Test
    @DisplayName("로그아웃을 한다.")
    void givenValid_whenLogout_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        String fcmToken = "fcm-token";

        // when
        userService.logout(user.getUsername(), fcmToken);

        // then
        Optional<Fcm> fcm = fcmRepository.findByUserAndFcmToken(user, fcmToken);
        assertThat(fcm).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 로그아웃시 예외가 발생한다.")
    void givenNonExistingUser_whenLogout_thenThrow() {
        // given
        String username = "tester";
        String fcmToken = "fcm-token";
        
        // when & then
        assertThatThrownBy(() -> userService.logout(username, fcmToken))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원 정보 조회를 한다.")
    void givenUsername_whenFindUserInfo_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when
        UserDefaultResponse response = userService.findUserInfo(user.getUsername());

        // then
        assertThat(response)
                .extracting("userId", "username", "nickname", "gender",
                        "birthdate", "isNotified", "createdAt", "updatedAt")
                .containsExactly(user.getId(), user.getUsername(), user.getNickname(), user.getGender(),
                        user.getBirthdate(), user.getIsNotified(), user.getCreatedAt(), user.getUpdatedAt());

        assertThat(response.getContact())
                .extracting("contactId", "email", "createdAt",
                        "updatedAt")
                .containsExactly(contact.getId(), contact.getEmail(), contact.getCreatedAt(),
                        contact.getUpdatedAt());
    }

    @Test
    @DisplayName("존재하지 않은 회원 정보 조회를 한다.")
    void givenNonExistingUsername_whenFindUserInfo_thenThrow() {
        // given
        String username = "tester";

        // when & then
        assertThatThrownBy(() -> userService.findUserInfo(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하는 FCM 토큰 업데이트를 한다.")
    void givenExistingFcmToken_whenUpdateFcmToken_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        Fcm fcm = createFcm(user);
        fcmRepository.save(fcm);

        String fcmToken = "fcm-token";
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when
        userService.updateFcmToken(user.getUsername(), fcmToken, lastRequestAt);

        // then
        Optional<Fcm> foundFcm = fcmRepository.findByUserAndFcmToken(user, fcmToken);
        assertEquals(fcmToken, foundFcm.get().getFcmToken());

        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        assertEquals(lastRequestAt, foundUser.get().getLastRequestAt());
    }

    @Test
    @DisplayName("존재하지 않은 FCM 토큰 업데이트를 한다.")
    void givenNonExistingFcmToken_whenUpdateFcmToken_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        String fcmToken = "fcm-token";
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when
        userService.updateFcmToken(user.getUsername(), fcmToken, lastRequestAt);

        // then
        Optional<Fcm> fcm = fcmRepository.findByUserAndFcmToken(user, fcmToken);
        assertEquals(fcmToken, fcm.get().getFcmToken());

        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        assertEquals(lastRequestAt, foundUser.get().getLastRequestAt());
    }

    @Test
    @DisplayName("존재하지 않은 회원의 FCM 토큰을 업데이트시 예외가 발생한다.")
    void givenNonExistingUser_whenUpdateFcmToken_thenThrow() {
        // given
        String username = "tester";
        String fcmToken = "fcm-token";
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> userService.updateFcmToken(username, fcmToken, lastRequestAt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }
    
    @Test
    @DisplayName("아이디를 이메일로 전송한다.")
    void givenValid_whenSendUsernameToEmail_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when & then
        userService.sendUsernameToEmail(contact.getEmail());
    }

    @Test
    @DisplayName("존재하지 않은 연락처로 아이디 이메일로 전송시 예외가 발생한다.")
    void givenNonRegisteredEmail_whenSendUsernameToEmail_thenThrow() {
        // given
        String email = "tester@gabojait.com";

        // when & then
        assertThatThrownBy(() -> userService.sendUsernameToEmail(email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CONTACT_NOT_FOUND);
    }

    @Test
    @DisplayName("비밀번호를 이메일로 전송한다.")
    void givenValid_whenSendPasswordToEmail_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        UserFindPasswordRequest request = createValidUserFindPasswordRequest();

        // when & then
        userService.sendPasswordToEmail(request);
    }

    @Test
    @DisplayName("동일하지 않은 이메일로 비밀번호를 이메일로 전송시 예외가 발생한다.")
    void givenUnMatchingEmail_whenSendPasswordToEmail_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        UserFindPasswordRequest request = createValidUserFindPasswordRequest();
        request.setEmail("tester2@gabojait.com");

        // when & then
        assertThatThrownBy(() -> userService.sendPasswordToEmail(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USERNAME_EMAIL_MATCH_INVALID);
    }

    @Test
    @DisplayName("가입하지 않은 이메일로 비밀번호를 이메일로 전송시 예외가 발생한다.")
    void givenUnregisteredUser_whenSendPasswordToEmail_thenThrow() {
        // given
        UserFindPasswordRequest request = createValidUserFindPasswordRequest();

        // when & then
        assertThatThrownBy(() -> userService.sendPasswordToEmail(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("비밀번호 검증을 한다.")
    void givenValid_whenVerifyPassword_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        String password = "password1!";

        // when & then
        userService.verifyPassword(user.getUsername(), password);
    }

    @Test
    @DisplayName("틀린 비밀번호로 비밀번호 검증시 예외가 발생한다.")
    void givenIncorrectPassword_whenVerifyPassword_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        String password = "password2!";

        // when & then
        assertThatThrownBy(() -> userService.verifyPassword(user.getUsername(), password))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PASSWORD_UNAUTHENTICATED);
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 비밀번호 검증시 예외가 발생한다.")
    void givenNonExistingUser_whenVerifyPassword_thenThrow() {
        // given
        String username = "tester";
        String password = "password1!";

        // when & then
        assertThatThrownBy(() -> userService.verifyPassword(username, password))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("닉네임 업데이트를 한다.")
    void givenValid_whenUpdateNickname_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        String nickname = "테스터이";

        // when
        userService.updateNickname(user.getUsername(), nickname);

        // then
        assertEquals(nickname, user.getNickname());
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 닉네임 업데이트를 하면 예외가 발생한다.")
    void givenNonExistingUser_whenUpdateNickname_thenThrow() {
        // given
        String username = "tester";
        String nickname = "테스터";

        // when & then
        assertThatThrownBy(() -> userService.updateNickname(username, nickname))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("'가보자잇'이 포함된 닉네임으로 닉네임 업데이트시 예외가 발생한다.")
    void givenGabojaitNickname_whenUpdateNickname_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        String nickname = "가보자잇크루";

        // when & then
        assertThatThrownBy(() -> userService.updateNickname(user.getUsername(), nickname))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(UNAVAILABLE_NICKNAME);
    }

    @Test
    @DisplayName("사용중인 닉네임으로 닉네임 업데이트시 예외가 발생한다.")
    void givenExistingNickname_whenUpdateNickname_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when & then
        assertThatThrownBy(() -> userService.updateNickname(user.getUsername(), user.getNickname()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EXISTING_NICKNAME);
    }

    @Test
    @DisplayName("비밀번호 업데이트를 한다.")
    void givenValid_whenUpdatePassword_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        String password = "password2!";

        // when & then
        userService.updatePassword(user.getUsername(), password, password);
    }

    @Test
    @DisplayName("동일하지 않은 비밀번호로 비밀번호 업데이트를 하면 예외가 발생한다.")
    void givenUnMatchingPassword_whenUpdatePassword_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        String password = "password2!";
        String passwordReEntered = "password3!";

        // when & then
        assertThatThrownBy(() ->
                userService.updatePassword(user.getUsername(), password, passwordReEntered))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PASSWORD_MATCH_INVALID);
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 비밀번호 업데이트를 하면 예외가 발생한다.")
    void givenNonExistingUser_whenUpdatePassword_thenThrow() {
        // given
        String username = "tester";
        String password = "password1!";

        // when & then
        assertThatThrownBy(() ->
                userService.updatePassword(username, password, password))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("알림 여부 업데이트를 한다.")
    void givenValid_whenUpdateIsNotified_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        boolean isNotified = false;

        // when
        userService.updateIsNotified(user.getUsername(), isNotified);

        // then
        assertFalse(user.getIsNotified());
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 알림 여부 업데이트를 하면 예외가 발생한다.")
    void givenNonExistingUser_whenUpdateIsNotified_thenThrow() {
        // given
        String username = "tester";
        boolean isNotified = false;

        // when & then
        assertThatThrownBy(() -> userService.updateIsNotified(username, isNotified))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원 탈퇴를 한다.")
    void givenValid_whenDeleteAccount_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String username = "tester";

        Contact contact = createContact(email);
        contact.verified();
        contactRepository.save(contact);

        User user = createUser(username, "테스터", contact);
        userRepository.save(user);

        Education education = createEducation("가보자잇대", user);
        educationRepository.save(education);

        Portfolio portfolio = createPortfolio("깃허브", user);
        portfolioRepository.save(portfolio);

        Skill skill = createSkill("스프링", user);
        skillRepository.save(skill);

        Work work = createWork("가보자잇사", user);
        workRepository.save(work);


        // when
        userService.deleteAccount(user.getUsername());

        // then
        assertThat(userRepository.findByUsername(username)).isEmpty();
        assertThat(contactRepository.findByEmail(email)).isEmpty();
        assertThat(fcmRepository.findAllByUser(user)).isEmpty();
        assertThat(notificationRepository.findAllByUser(user)).isEmpty();
        assertThat(userRoleRepository.findAll(username)).isEmpty();
        assertThat(educationRepository.findAll(user.getId())).isEmpty();
        assertThat(portfolioRepository.findAll(user.getId())).isEmpty();
        assertThat(skillRepository.findAll(user.getId())).isEmpty();
        assertThat(workRepository.findAll(user.getId())).isEmpty();
        assertThat(teamMemberRepository.findAllFetchTeam(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 회원 탈퇴를 하면 예외가 발생한다.")
    void givenNonExistingUser_whenDeleteAccount_thenThrow() {
        // given
        String username = "tester";

        // when & then
        assertThatThrownBy(() -> userService.deleteAccount(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    private UserLoginRequest createValidUserLoginRequest() {
        return UserLoginRequest.builder()
                .username("tester")
                .password("password1!")
                .fcmToken("fcm-token")
                .build();
    }

    private UserFindPasswordRequest createValidUserFindPasswordRequest() {
        return UserFindPasswordRequest.builder()
                .email("tester@gabojait.com")
                .username("tester")
                .build();
    }

    private UserRegisterRequest createValidUserRegisterRequest() {
        return UserRegisterRequest.builder()
                .username("tester")
                .password("password1!")
                .passwordReEntered("password1!")
                .nickname("테스터")
                .gender(Gender.M.name())
                .birthdate(LocalDate.of(1997, 2, 11))
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .fcmToken("fcm-token")
                .build();
    }

    private Contact createContact(String email) {
        return Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
    }

    private User createUser(String username, String nickname, Contact contact) {
        return User.builder()
                .username(username)
                .password(passwordUtility.encodePassword("password1!"))
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(LocalDateTime.now())
                .contact(contact)
                .build();
    }

    private UserRole createUserRole(User user) {
        return UserRole.builder()
                .user(user)
                .role(Role.USER)
                .build();
    }

    private Fcm createFcm(User user) {
        return Fcm.builder()
                .user(user)
                .fcmToken("fcm-token")
                .build();
    }

    private Education createEducation(String institutionName, User user) {
        return Education.builder()
                .institutionName(institutionName)
                .startedAt(LocalDate.of(2019, 3, 1))
                .endedAt(LocalDate.of(2023,8, 1))
                .isCurrent(false)
                .user(user)
                .build();
    }

    private Portfolio createPortfolio(String portfolioName, User user) {
        return Portfolio.builder()
                .portfolioName(portfolioName)
                .portfolioUrl("github.com/gabojait")
                .media(Media.LINK)
                .user(user)
                .build();
    }

    private Skill createSkill(String skillName, User user) {
        return Skill.builder()
                .skillName(skillName)
                .level(Level.MID)
                .isExperienced(true)
                .user(user)
                .build();
    }

    private Work createWork(String corporationName, User user) {
        return Work.builder()
                .corporationName(corporationName)
                .workDescription("가보자잇사에서 백엔드 개발")
                .startedAt(LocalDate.of(2022, 8, 1))
                .isCurrent(true)
                .user(user)
                .build();
    }
}