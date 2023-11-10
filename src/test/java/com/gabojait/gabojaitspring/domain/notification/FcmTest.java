package com.gabojait.gabojaitspring.domain.notification;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FcmTest {

    @Test
    @DisplayName("FCM을 생성한다.")
    void builder() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        String fcmToken = "fcm-token";

        // when
        Fcm fcm = createFcm(fcmToken, user);

        // then
        assertEquals(fcm.getFcmToken(), fcmToken);
    }

    private static Stream<Arguments> providerEquals() {
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        String fcmToken = "fcm-token";

        Fcm fcm = createFcm(fcmToken, user);

        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Fcm userFcm1 = createFcm(fcmToken, user1);
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Fcm userFcm2 = createFcm(fcmToken, user2);

        return Stream.of(
                Arguments.of(fcm, fcm, true),
                Arguments.of(fcm, new Object(), false),
                Arguments.of(
                        createFcm("fcm-token1", user),
                        createFcm("fcm-token2", user),
                        false
                ),
                Arguments.of(userFcm1, userFcm2, false)
        );
    }

    @ParameterizedTest(name = "[{index}] FCM 객체를 비교한다.")
    @MethodSource("providerEquals")
    @DisplayName("FCM 객체를 비교한다.")
    void givenProvider_whenEquals_thenReturn(Fcm fcm, Object object, boolean result) {
        // when & then
        assertThat(fcm.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        return Stream.of(
                Arguments.of(
                        createFcm("fcm-token", user),
                        createFcm("fcm-token", user),
                        true
                ),
                Arguments.of(
                        createFcm("fcm-token1", user),
                        createFcm("fcm-token2", user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] FCM 해시코드를 비교한다.")
    @MethodSource("providerHashCode")
    @DisplayName("FCM 해시코드를 비교한다.")
    void givenProvider_whenHashCode_thenReturn(Fcm fcm1, Fcm fcm2, boolean result) {
        // when
        int hashCode1 = fcm1.hashCode();
        int hashCode2 = fcm2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Fcm createFcm(String fcmToken, User user) {
        return Fcm.builder()
                .fcmToken(fcmToken)
                .user(user)
                .build();
    }

    private static User createDefaultUser(String email,
                                   String verificationCode,
                                   String username,
                                   String password,
                                   String nickname,
                                   Gender gender,
                                   LocalDate birthdate,
                                   LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}