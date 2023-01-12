package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.dto.ContactSaveRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactVerificationRequestDto;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;

    /**
     * 이메일 중복 확인 |
     * 이메일로 이미 가입된 유저가 있는지 확인한다. |
     * 400: 이미 가입한 경우 에러
     * 500: 연락처 정보 저장 중 서버 에러
     */
    public void isExistingEmail(String email) {
        log.info("INITIALIZE | ContactService | isExistingEmail | " + email);
        LocalDateTime initTime = LocalDateTime.now();

        contactRepository.findByEmail(email)
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

        log.info("COMPLETE | ContactService | isExistingEmail | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + email);
    }

    /**
     * 연락처 저장 |
     * 이메일이 존재하는지 확인하고, 이메일에 대한 정보를 저장한다. |
     * 500: 연락처 정보 저장 중 서버 에러
     */
    public void save(ContactSaveRequestDto request) {
        log.info("INITIALIZE | ContactService | save | " + request.getEmail());
        LocalDateTime initTime = LocalDateTime.now();

        try {
            Contact contact = contactRepository.save(request.toEntity(generateVerificationCode()));

            emailService.sendEmail(
                    contact.getEmail(),
                    "[가보자it] 인증번호",
                    "회원님 안녕하세요!🙇🏻<br>가입 절차를 계속하기 위해 아래의 번호를 이메일 인증번호란에 입력해주세요.🙏🏻",
                    contact.getVerificationCode()
            );

            log.info("COMPLETE | ContactService | save | " + Duration.between(initTime, LocalDateTime.now()) +
                    " | " + contact.getEmail());
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

        log.info("PROGRESS | ContactService | generateVerificationCode | " + sb);
        return sb.toString();
    }

    /**
     * 인증번호 확인 |
     * 이메일로 수신한 인증번호를 확인한다. |
     * 400: 존재하지 않은 이메일이거나 틀린 인증번호인 에러
     * 500: 연락처 정보 저장 중 서버 에러
     */
    public void verification(ContactVerificationRequestDto request) {
        log.info("INITIALIZE | ContactService | verification | " + request.getEmail());
        LocalDateTime initTime = LocalDateTime.now();

        contactRepository.findByEmail(request.getEmail())
                .ifPresentOrElse(contact -> {
                    if (contact.getVerificationCode().equals(request.getVerificationCode())) {
                        contact.setIsVerified(true);

                        try {
                            contactRepository.save(contact);
                        } catch (RuntimeException e) {
                            throw new CustomException(SERVER_ERROR);
                        }

                        log.info("COMPLETE | ContactService | verification | " +
                                Duration.between(initTime, LocalDateTime.now()) + " | " + contact.getEmail());
                    } else {
                        throw new CustomException(VERIFICATION_CODE_INCORRECT);
                    }
                }, () -> {
                    throw new CustomException(NOT_VERIFIED_EMAIL);
                });
    }

    /**
     * 이메일로 단건 조회 |
     * 이메일로 연락처를 조회하여 반환한다. |
     * 404: 이메일 인증을 하지 않았을 에러
     */
    public Contact findOneByEmail(String email) {
        log.info("INITIALIZE | ContactService | findOneByEmail | " + email);
        LocalDateTime initTime = LocalDateTime.now();

        Contact contact = contactRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new CustomException(NOT_VERIFIED_EMAIL);
                });

        if (!contact.getIsVerified() && contact.getIsRegistered()) {
            throw new CustomException(NOT_VERIFIED_EMAIL);
        }

        log.info("COMPLETE | ContactService | findOneByEmail | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + contact.getEmail());
        return contact;
    }

    /**
     * 회원 가입 |
     * 연락처에 회원가입 여부를 true 로 바꾼다. |
     * 500: 연락처 정보 저장 중 서버 에러
     */
    public void register(Contact contact) {
        log.info("INITIALIZE | ContactService | register | " + contact.getEmail());
        LocalDateTime initTime = LocalDateTime.now();

        contact.setIsRegistered(true);

        try {
            contactRepository.save(contact);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ContactService | register | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + contact.getEmail());
    }

    /**
     * 연락처 전체 삭제 |
     * 500: 연락처 정보 삭제 중 서버 에러
     * TODO: 배포 전 삭제 필요
     */
    public void deleteAll() {
        log.info("INITIALIZE | ContactService | deleteAll");
        LocalDateTime initTime = LocalDateTime.now();

        try {
            contactRepository.deleteAll();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ContactService | deleteAll | " + Duration.between(initTime, LocalDateTime.now()));
    }
}
