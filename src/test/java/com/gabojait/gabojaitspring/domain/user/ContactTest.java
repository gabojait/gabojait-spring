package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ContactTest {

    @Test
    @DisplayName("연락처 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";

        // when
        Contact contact = createContact(email, verificationCode);

        // then
        assertThat(contact)
                .extracting("email", "verificationCode", "isVerified")
                .containsExactly(email, verificationCode, false);
    }

    @Test
    @DisplayName("연락처 인증이 정상 작동한다")
    void givenValid_whenVerified_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        Contact contact = createContact(email, verificationCode);

        // when
        contact.verified();

        // then
        assertThat(contact)
                .extracting("email", "verificationCode", "isVerified")
                .containsExactly(email, verificationCode, true);
    }

    private static Stream<Arguments> providerEquals() {
        Contact contact = createContact("tester@gabojait.com", "000000");
        Contact verifiedContact = createContact("tester@gabojait.com", "000000");
        verifiedContact.verified();

        return Stream.of(
                Arguments.of(contact, contact, true),
                Arguments.of(contact, new Object(), false),
                Arguments.of(
                        createContact("tester@gabojait.com", "000000"),
                        createContact("tester@gabojait.com", "000000"),
                        true
                ),
                Arguments.of(
                        createContact("tester1@gabojait.com", "000000"),
                        createContact("tester2@gabojait.com", "000000"),
                        false
                ),
                Arguments.of(
                        createContact("tester@gabojait.com", "000000"),
                        createContact("tester@gabojait.com", "000001"),
                        false
                ),
                Arguments.of(contact, verifiedContact, false)
        );
    }

    @ParameterizedTest(name = "[{index}] 연락처 객체를 비교한다")
    @MethodSource("providerEquals")
    @DisplayName("연락처 객체 비교이 정상 작동한다")
    void givenProvider_whenEquals_thenReturn(Contact contact, Object object, boolean result) {
        // when & then
        assertThat(contact.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        return Stream.of(
                Arguments.of(
                        createContact("tester@gabojait.com", "0000000"),
                        createContact("tester@gabojait.com", "0000000"),
                        true
                ),
                Arguments.of(
                        createContact("tester1@gabojait.com", "0000000"),
                        createContact("tester2@gabojait.com", "0000000"),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 연락처 해시코드를 비교한다")
    @MethodSource("providerHashCode")
    @DisplayName("연락처 해시코드 비교가 정상 작동한다")
    void givenProvider_whenHashCode_thenReturn(Contact contact1, Contact contact2, boolean result) {
        // when
        int hashCode1 = contact1.hashCode();
        int hashCode2 = contact2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Contact createContact(String email, String verificationCode) {
        return Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
    }
}