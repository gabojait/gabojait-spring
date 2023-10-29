package com.gabojait.gabojaitspring.api.service.user;

import com.gabojait.gabojaitspring.api.dto.user.request.ContactCreateRequest;
import com.gabojait.gabojaitspring.api.dto.user.request.ContactVerifyRequest;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ContactServiceTest {

    @Autowired private ContactService contactService;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("연락처 중복 검증을 한다.")
    void givenValid_whenValidateDuplicateContact_thenReturn() {
        // given
        String email = "tester@gabojait.com";

        // when
        contactService.validateDuplicateContact(email);

        // then
        Optional<Contact> contact = contactRepository.findByEmail(email);

        assertThat(contact).isEmpty();
    }

    @Test
    @DisplayName("존재하는 연락처 중복 검증시 기존 연락처를 삭제한다.")
    void givenExistingContact_whenValidateDuplicateContact_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contactRepository.save(contact);

        // when
        contactService.validateDuplicateContact(contact.getEmail());

        // then
        Optional<Contact> foundContact = contactRepository.findByEmail(contact.getEmail());

        assertThat(foundContact).isEmpty();
    }

    @Test
    @DisplayName("회원가입된 연락처 중복 검증시 예외가 발생한다.")
    void givenRegisteredContact_whenValidateDuplicateContact_thenThrow() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contactRepository.save(contact);
        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when & then
        assertThatThrownBy(() -> contactService.validateDuplicateContact(contact.getEmail()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EXISTING_CONTACT);
    }

    @Test
    @DisplayName("연락처 생성 요청시 연락처를 생성한다.")
    void givenValid_whenCreateContact_thenReturn() {
        // given
        ContactCreateRequest request = createValidContactCreateRequest();

        // when
        contactService.createContact(request);

        // then
        Optional<Contact> contact = contactRepository.findByEmail(request.getEmail());

        assertThat(contact.get())
                .extracting("email", "isVerified")
                .containsExactly(request.getEmail(), false);
    }

    @Test
    @DisplayName("존재하는 연락처 생성 요청시 예외가 발생한다.")
    void givenExistingContact_whenCreateContact_thenThrow() {
        // given
        ContactCreateRequest request = createValidContactCreateRequest();

        Contact contact = createContact(request.getEmail());
        contactRepository.save(contact);

        // when & then
        assertThatThrownBy(() -> contactService.createContact(request))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("연락처 인증코드 확인 요청시 연락처를 인증한다.")
    void givenValid_whenVerifyContact_thenReturn() {
        // given
        ContactVerifyRequest request = createValidContactVerifyRequest("000000");

        Contact contact = createContact(request.getEmail());
        contactRepository.save(contact);

        // when
        contactService.verifyContact(request);

        // then
        Optional<Contact> foundContact = contactRepository.findByEmailAndIsVerified(request.getEmail(), true);

        assertThat(foundContact.get())
                .extracting("email", "verificationCode", "isVerified")
                .containsExactlyInAnyOrder(request.getEmail(), request.getVerificationCode(), true);
    }

    @Test
    @DisplayName("틀린 인증코드로 연락처 인증코드 확인 요청시 예외가 발생한다.")
    void givenInvalidVerificationCode_whenVerifyContact_thenThrow() {
        // given
        ContactVerifyRequest request = createValidContactVerifyRequest("000001");

        Contact contact = createContact(request.getEmail());
        contactRepository.save(contact);

        // when & then
        assertThatThrownBy(() -> contactService.verifyContact(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(VERIFICATION_CODE_INVALID);
    }

    @Test
    @DisplayName("존재하지 않은 연락처 인증 요청시 예외가 발생한다.")
    void givenNonExistingContact_whenVerifyContact_thenThrow() {
        // given
        ContactVerifyRequest request = createValidContactVerifyRequest("000000");

        // when & then
        assertThatThrownBy(() -> contactService.verifyContact(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EMAIL_NOT_FOUND);
    }

    private ContactCreateRequest createValidContactCreateRequest() {
        return ContactCreateRequest.builder()
                .email("tester@gabojait.com")
                .build();
    }

    private ContactVerifyRequest createValidContactVerifyRequest(String verificationCode) {
        return ContactVerifyRequest.builder()
                .email("tester@gabojait.com")
                .verificationCode(verificationCode)
                .build();
    }

    private Contact createContact(String email) {
        return Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
    }

    private User createUser(String username,
                            String nickname,
                            Contact contact) {
        return User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(LocalDateTime.now())
                .contact(contact)
                .build();
    }
}