package com.gabojait.gabojaitspring.user.service;

import com.gabojait.gabojaitspring.common.util.EmailProvider;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.dto.req.ContactSaveReqDto;
import com.gabojait.gabojaitspring.user.dto.req.ContactVerifyReqDto;
import com.gabojait.gabojaitspring.user.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailProvider emailProvider;
    private final GeneralProvider generalProvider;

    /**
     * 인증 코드 전송 |
     * 409(EXISTING_CONTACT)
     * 500(SERVER_ERROR / EMAIL_SEND_ERROR)
     */
    public void sendRegisterVerificationCode(ContactSaveReqDto request) {
        validateExistingContact(request.getEmail());

        String verificationCode = generalProvider.generateRandomCode(6);
        Contact contact = request.toEntity(verificationCode);
        saveContact(contact);

        sendEmail(contact);
    }

    /**
     * 인증코드 확인 |
     * 400(VERIFICATION_CODE_INVALID)
     * 404(EMAIL_NOT_FOUND)
     */
    public void verify(ContactVerifyReqDto request) {
        Contact contact = findOneUnverifiedUnregisteredContact(request.getEmail());

        if (!contact.getVerificationCode().equals(request.getVerificationCode()))
            throw new CustomException(VERIFICATION_CODE_INVALID);

        contact.verified();
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
    private void saveContact(Contact contact) {
        try {
            contactRepository.save(contact);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 연락처 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    public void hardDeleteContact(Contact contact) {
        try {
            contactRepository.delete(contact);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 인증되지 않고 가입되지 않은 연락처 단건 조회 |
     * 404(EMAIL_NOT_FOUND)
     */
    private Contact findOneUnverifiedUnregisteredContact(String email) {
        return contactRepository.findByEmailAndIsVerifiedIsFalseAndIsDeletedIsFalse(email)
                .orElseThrow(() -> {
                    throw new CustomException(EMAIL_NOT_FOUND);
                });
    }

    /**
     * 이메일 중복 검증 |
     * 409(EXISTING_CONTACT)
     * 500(SERVER_ERROR)
     */
    private void validateExistingContact(String email) {
        contactRepository.findByEmailAndIsDeletedIsFalse(email)
                .ifPresent(c -> {
                    if (c.getUser() != null)
                        throw new CustomException(EXISTING_CONTACT);
                    else
                        hardDeleteContact(c);
                });
    }
}
