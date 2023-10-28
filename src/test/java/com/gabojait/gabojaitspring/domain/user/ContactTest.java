package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    @DisplayName("연락처를 생성한다.")
    void builder() {
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
    @DisplayName("연락처 인증을 한다.")
    void verified() {
        // given
        Contact contact = createContact("tester@gabojait.com", "000000");

        // when
        contact.verified();

        // then
        assertTrue(contact.getIsVerified());
    }

    @Test
    @DisplayName("같은 객체인 연락처를 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com", "000000");

        // when
        boolean result = contact.equals(contact);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("같은 정보인 연락처를 비교하면 동일하다.")
    void givenEqualData_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        Contact contact1 = createContact(email, verificationCode);
        Contact contact2 = createContact(email, verificationCode);

        // when
        boolean result = contact1.equals(contact2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("다른 객체인 연락처를 비교하면 동일하지 않다.")
    void givenUnequalInstance_whenEquals_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com", "000000");
        Object object = new Object();

        // when
        boolean result = contact.equals(object);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 이메일인 연락처를 비교하면 동일하지 않다.")
    void givenUnequalEmail_whenEquals_thenReturn() {
        // given
        String email1 = "tester1@gabojait.com";
        String email2 = "tester2@gabojait.com";

        String verificationCode = "000000";
        Contact contact1 = createContact(email1, verificationCode);
        Contact contact2 = createContact(email2, verificationCode);

        // when
        boolean result = contact1.equals(contact2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 인증 코드인 연락처를 비교하면 동일하지 않다.")
    void givenUnequalVerificationCode_whenEquals_thenReturn() {
        // given
        String verificationCode1 = "000000";
        String verificationCode2 = "000001";

        String email = "tester@gabojait.com";
        Contact contact1 = createContact(email, verificationCode1);
        Contact contact2 = createContact(email, verificationCode2);

        // when
        boolean result = contact1.equals(contact2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 인증 여부인 연락처를 비교하면 동일하지 않다.")
    void givenUnequalIsVerified_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        Contact contact1 = createContact(email, verificationCode);
        Contact contact2 = createContact(email, verificationCode);

        contact1.verified();

        // when
        boolean result = contact1.equals(contact2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 연락처의 해시코드는 같다.")
    void givenEqual_whenHashCode_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        Contact contact1 = createContact(email, verificationCode);
        Contact contact2 = createContact(email, verificationCode);

        // when
        int hashCode1 = contact1.hashCode();
        int hashCode2 = contact2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("동일하지 않은 연락처의 해시코드는 다르다.")
    void givenUnequal_whenHashCode_thenReturn() {
        // given
        String email1 = "tester1@gabojait.com";
        String email2 = "tester2@gabojait.com";

        String verificationCode = "000000";
        Contact contact1 = createContact(email1, verificationCode);
        Contact contact2 = createContact(email2, verificationCode);

        // when
        int hashCode1 = contact1.hashCode();
        int hashCode2 = contact2.hashCode();

        // then
        assertThat(hashCode1).isNotEqualTo(hashCode2);
    }

    private Contact createContact(String email, String verificationCode) {
        return Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
    }
}