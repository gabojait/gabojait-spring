package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.domain.user.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ContactRepositoryTest {

    @Autowired private ContactRepository contactRepository;

    @Test
    @DisplayName("이메일로 연락처 단건 조회를 한다.")
    void givenValid_whenFindByEmail_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");

        contactRepository.save(contact);

        // when
        Optional<Contact> foundContact = contactRepository.findByEmail(contact.getEmail());

        // then
        assertThat(foundContact.get())
                .extracting("id", "email", "verificationCode", "isVerified", "createdAt", "updatedAt")
                .containsExactly(contact.getId(), contact.getEmail(), contact.getVerificationCode(),
                        contact.getIsVerified(), contact.getCreatedAt(), contact.getUpdatedAt());
    }

    @Test
    @DisplayName("이메일로 인증된 연락처 단건 조회를 한다.")
    void givenVerifiedContact_whenFindByEmailAndIsVerified_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();

        contactRepository.save(contact);

        // when
        Optional<Contact> foundContact = contactRepository.findByEmailAndIsVerified(contact.getEmail(), true);

        // then
        assertThat(foundContact.get())
                .extracting("id", "email", "verificationCode", "isVerified", "createdAt", "updatedAt")
                .containsExactly(contact.getId(), contact.getEmail(), contact.getVerificationCode(),
                        contact.getIsVerified(), contact.getCreatedAt(), contact.getUpdatedAt());
    }

    @Test
    @DisplayName("이메일로 인증 안된 연락처 단건 조회를 한다.")
    void givenUnverifiedContact_whenFindByEmailAndIsVerifiedIsFalse_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");

        contactRepository.save(contact);

        // when
        Optional<Contact> foundContact = contactRepository.findByEmailAndIsVerified(contact.getEmail(), false);

        // then
        assertThat(foundContact.get())
                .extracting("id", "email", "verificationCode", "isVerified", "createdAt", "updatedAt")
                .containsExactly(contact.getId(), contact.getEmail(), contact.getVerificationCode(),
                        contact.getIsVerified(), contact.getCreatedAt(), contact.getUpdatedAt());
    }

    @Test
    @DisplayName("동일한 이메일 저장시 예외가 발생한다.")
    void givenSameEmails_whenSave_thenThrow() {
        // given
        Contact contact1 = createContact("tester@gabojait.com");
        Contact contact2 = createContact("tester@gabojait.com");

        // when & then
        assertThatThrownBy(() -> contactRepository.saveAll(List.of(contact1, contact2)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Contact createContact(String email) {
        return Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
    }
}