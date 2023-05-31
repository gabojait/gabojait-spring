package com.gabojait.gabojaitspring.user.service;

import com.gabojait.gabojaitspring.common.util.EmailProvider;
import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.dto.req.ContactSaveReqDto;
import com.gabojait.gabojaitspring.user.dto.req.ContactVerifyReqDto;
import com.gabojait.gabojaitspring.user.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailProvider emailProvider;
    private final UtilityProvider utilityProvider;

    /**
     * 인증 코드 전송 | main |
     * 409(EXISTING_CONTACT)
     * 500(SERVER_ERROR / EMAIL_SEND_ERROR)
     */
    public void sendRegisteredVerificationCode(ContactSaveReqDto request) {
        validateExistingContact(request.getEmail());

        String verificationCode = utilityProvider.generateRandomCode(6);
        Contact contact = request.toEntity(verificationCode);
        save(contact);

        sendEmail(contact);
    }

    /**
     * 인증 코드 확인 | main |
     * 400(VERIFICATION_CODE_INVALID)
     * 404(EMAIL_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void verify(ContactVerifyReqDto request) {
        Contact contact = findOneUnverifiedAndUnregisteredByEmail(request.getEmail());

        if (!contact.getVerificationCode().equals(request.getVerificationCode()))
            throw new CustomException(VERIFICATION_CODE_INVALID);

        contact.verified();
        save(contact);
    }

    /**
     * 인증되고 가입되지 않은 연락처 단건 조회 | main |
     * 404(CONTACT_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Contact registerContact(String email) {
        Contact contact = findOneVerifiedAndUnregisteredByEmail(email);

        contact.registered();
        save(contact);

        return contact;
    }

    /**
     * 이메일로 가입된 연락처 단건 조회 | main |
     * 404(CONTACT_NOT_FOUND)
     */
    public Contact findOneRegisteredByEmail(String email) {
        return contactRepository.findByEmailAndIsVerifiedIsTrueAndIsRegisteredIsTrueAndIsDeletedIsFalse(email)
                .orElseThrow(() -> {
                    throw new CustomException(CONTACT_NOT_FOUND);
                });
    }

    /**
     * 인증되고 가입되지 않은 연락처 단건 조회 |
     * 404(CONTACT_NOT_FOUND)
     */
    private Contact findOneVerifiedAndUnregisteredByEmail(String email) {
        return contactRepository.findByEmailAndIsVerifiedIsTrueAndIsRegisteredIsFalseAndIsDeletedIsFalse(email)
                .orElseThrow(() -> {
                    throw new CustomException(CONTACT_NOT_FOUND);
                });
    }

    /**
     * 인증되지 않고 가입되지 않은 연락처 단건 조회 |
     * 404(EMAIL_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    private Contact findOneUnverifiedAndUnregisteredByEmail(String email) {
        try {
            Optional<Contact> contact = contactRepository.findByEmailAndIsVerifiedIsFalseAndIsDeletedIsFalse(email);

            if (contact.isEmpty())
                throw new CustomException(EMAIL_NOT_FOUND);

            return contact.get();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 가입 인증코드 전송 |
     * 500(EMAIL_SEND_ERROR)
     */
    private void sendEmail(Contact contact) {
        emailProvider.sendEmail(
                contact.getEmail(),
                "[가보자IT] 인증코드",
                "안녕하세요!🙇🏻<br>가입 절차를 계속하기 위해 아래의 코드를 이메일 인증코드란에 입력해주세요.",
                contact.getVerificationCode()
        );
    }

    /**
     * 연락처 저장 |
     * 500(SERVER_ERROR)
     */
    public Contact save(Contact contact) {
        try {
            return contactRepository.save(contact);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 연락처 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    private void hardDelete(Contact contact) {
        try {
            contactRepository.delete(contact);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 연락처 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    public void softDelete(Contact contact) {
        contact.delete();

        save(contact);
    }

    /**
     * 이메일 중복 검증 |
     * 409(EXISTING_CONTACT)
     * 500(SERVER_ERROR)
     */
    private void validateExistingContact(String email) {
        contactRepository.findByEmailAndIsDeletedIsFalse(email)
                .ifPresent(c -> {
                    if (c.getIsRegistered())
                        throw new CustomException(EXISTING_CONTACT);
                    else
                        hardDelete(c);
                });
    }
}
