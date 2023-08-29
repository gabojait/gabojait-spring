package com.gabojait.gabojaitspring.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ContactTest {

    @Test
    @DisplayName("연락처 | 빌더")
    void builder() {
        // given
        String email = "test@gabojait.com";
        String verificationCode = "000000";

        // when
        Contact contact = createContact(email, verificationCode);

        // then
        assertThat(contact)
                .extracting("email", "verificationCode", "isVerified", "isDeleted", "user")
                .containsExactly(email, verificationCode, false, false, null);
    }

    @Test
    @DisplayName("연락처 | 인증")
    void Verified() {
        // given
        Contact contact = createContact("test@gabojait.com", "000000");

        // when
        contact.verified();

        // then
        assertThat(contact.getIsVerified()).isTrue();
    }

    @Test
    @DisplayName("연락처 | 삭제")
    void delete() {
        // given
        Contact contact = createContact("test@gabojait.com", "000000");

        // when
        contact.delete();

        // then
        assertThat(contact)
                .extracting("email", "isDeleted")
                .containsExactly(null, true);

    }

    private Contact createContact(String email, String verificationCode) {
        return Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
    }
}