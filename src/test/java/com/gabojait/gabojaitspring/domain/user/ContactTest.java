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
        String email = "test@gabojait.com";
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
        Contact contact = createContact("test@gabojait.com", "000000");

        // when
        contact.verified();

        // then
        assertTrue(contact.getIsVerified());
    }

    private Contact createContact(String email, String verificationCode) {
        return Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
    }
}