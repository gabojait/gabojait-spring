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
        Contact foundContact = contactRepository.findByEmail(contact.getEmail()).get();

        // then
        assertThat(foundContact).isEqualTo(contact);
    }

    @Test
    @DisplayName("이메일로 인증된 연락처 단건 조회를 한다.")
    void givenVerified_whenFindByEmailAndIsVerified_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();

        contactRepository.save(contact);

        // when
        Contact foundContact = contactRepository.findByEmailAndIsVerified(contact.getEmail(), true).get();

        // then
        assertThat(foundContact).isEqualTo(contact);
    }

    @Test
    @DisplayName("이메일로 인증되지 않은 연락처 단건 조회를 한다.")
    void givenUnverified_whenFindByEmailAndIsVerified_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contactRepository.save(contact);

        // when
        Contact foundContact = contactRepository.findByEmailAndIsVerified(contact.getEmail(), false).get();

        // then
        assertThat(foundContact).isEqualTo(contact);
    }

    @Test
    @DisplayName("존재하는 연락처 이메일로 연락처 존재 여부를 확인한다.")
    void givenExisting_whenExistsByEmail_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contactRepository.save(contact);

        // when
        boolean result = contactRepository.existsByEmail(contact.getEmail());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 연락처 이메일로 연락처 존재 여부를 확인한다.")
    void givenNonExisting_whenExistsByEmail_thenReturn() {
        // given
        String email = "tester@gabojait.com";

        // when
        boolean result = contactRepository.existsByEmail(email);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이메일로 연락처를 삭제한다.")
    void givenValid_whenDeleteByEmail_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contactRepository.save(contact);

        // when
        contactRepository.deleteByEmail(contact.getEmail());

        // then
        assertThat(contactRepository.existsByEmail(contact.getEmail())).isFalse();
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