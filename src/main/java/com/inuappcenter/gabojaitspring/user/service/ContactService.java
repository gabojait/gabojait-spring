package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.dto.req.ContactSaveReqDto;
import com.inuappcenter.gabojaitspring.user.dto.req.ContactVerificationReqDto;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;

    /**
     * 이메일 중복 확인 |
     * 409(EXISTING_EMAIL)
     * 500(SERVER_ERROR)
     */
    public void isExistingEmail(String email) {

        contactRepository.findByEmailAndIsDeletedIsFalse(email)
                .ifPresent(contact -> {
                    if (contact.getIsRegistered()) {
                        throw new CustomException(EXISTING_EMAIL);
                    }

                    try {
                        contactRepository.delete(contact);
                    } catch (CustomException e) {
                        throw new CustomException(SERVER_ERROR);
                    }
                });
    }

    /**
     * 연락처 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void save(ContactSaveReqDto request) {

        try {
            Contact contact = contactRepository.save(request.toEntity(generateVerificationCode()));

            emailService.sendEmail(
                    contact.getEmail(),
                    "[가보자it] 인증번호",
                    "회원님 안녕하세요!🙇🏻<br>가입 절차를 계속하기 위해 아래의 번호를 이메일 인증번호란에 입력해주세요.🙏🏻",
                    contact.getVerificationCode()
            );
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 인증번호 생성 |
     * 숫자, 대문자 영문, 소문자 영문의 6가지 조합을 생성해 반환한다.
     */
    private String generateVerificationCode() {

        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 이메일로 인증안된 연락처 단건 조회 |
     * 409(EMAIL_VERIFICATION_INVALID)
     */
    public Contact findOneUnverifiedByEmail(String email) {

        return contactRepository.findByEmailAndIsVerifiedIsFalseAndIsDeletedIsFalse(email)
                .orElseThrow(() -> {
                    throw new CustomException(EMAIL_VERIFICATION_INVALID);
                });
    }

    /**
     * 회원 가입
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void register(Contact contact) {

        try {
            contact.registered();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 인증번호 확인 |
     * 400(VERIFICATIONCODE_INVALID)
     * 404(EMAIL_VERIFICATION_INVALID)
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void verification(ContactVerificationReqDto request) {

        contactRepository.findByEmailAndIsVerifiedIsFalseAndIsDeletedIsFalse(request.getEmail())
                .ifPresentOrElse(contact -> {
                    if (contact.getVerificationCode().equals(request.getVerificationCode())) {
                        try {
                            contact.verified();
                        } catch (RuntimeException e) {
                            throw new CustomException(SERVER_ERROR);
                        }
                    } else {
                        throw new CustomException(VERIFICATIONCODE_INVALID);
                    }
                }, () -> {
                    throw new CustomException(EMAIL_VERIFICATION_INVALID);
                });
    }

    /**
     * 이메일로 가입된 연락처 단건 조회 |
     * 404(EMAIL_NOT_FOUND)
     */
    public Contact findOneRegisteredByEmail(String email) {

        return contactRepository.findByEmailAndIsDeletedIsFalseAndIsRegisteredIsTrue(email)
                .orElseThrow(() -> {
                    throw new CustomException(EMAIL_NOT_FOUND);
                });
    }

    /**
     * 탈퇴 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void deactivate(Contact contact) {

        try {
            contact.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
