package com.gabojait.gabojaitspring.api.service.user;

import com.gabojait.gabojaitspring.api.dto.user.request.ContactCreateRequest;
import com.gabojait.gabojaitspring.api.dto.user.request.ContactVerifyRequest;
import com.gabojait.gabojaitspring.common.util.EmailUtility;
import com.gabojait.gabojaitspring.common.util.PasswordUtility;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final PasswordUtility passwordUtility;
    private final EmailUtility emailUtility;

    /**
     * 연락처 중복 검증 |
     * 409(EXISTING_CONTACT)
     * @param email 이메일
     */
    @Transactional
    public void validateDuplicateContact(String email) {
        boolean isExist = contactRepository.existsByEmail(email);

        if (isExist)
            userRepository.find(email)
                    .ifPresentOrElse(user -> {
                        throw new CustomException(EXISTING_CONTACT);
                        }, () -> contactRepository.deleteByEmail(email)
                    );
    }

    /**
     * 연락처 생성 |
     * 500(EMAIL_SEND_ERROR)
     * @param request 연락처 생성 요청
     */
    @Transactional
    public void createContact(ContactCreateRequest request) {
        String verificationCode = passwordUtility.generateRandomCode(6);
        Contact contact = request.toEntity(verificationCode);
        contactRepository.save(contact);

        emailUtility.sendEmail(
                contact.getEmail(),
                "[가보자IT] 인증코드",
                "안녕하세요!🙇🏻<br>가입 절차를 계속하기 위해 아래의 코드를 이메일 인증코드란에 입력해 주세요.",
                contact.getVerificationCode()
        );
    }

    /**
     * 인증코드 확인 |
     * 400(VERIFICATION_CODE_INVALID)
     * 404(EMAIL_NOT_FOUND)
     * @param request 연락처 인증코드 확인 요청
     */
    @Transactional
    public void verifyContact(ContactVerifyRequest request) {
        Contact contact = findUnverifiedContact(request.getEmail());

        if (!contact.getVerificationCode().equals(request.getVerificationCode()))
            throw new CustomException(VERIFICATION_CODE_INVALID);

        contact.verified();
    }

    /**
     * 미인증 연락처 단건 조회 |
     * 404(EMAIL_NOT_FOUND)
     * @param email 이메일
     * @return 연락처
     */
    private Contact findUnverifiedContact(String email) {
        return contactRepository.findByEmailAndIsVerified(email, false)
                .orElseThrow(() -> {
                    throw new CustomException(EMAIL_NOT_FOUND);
                });
    }
}
