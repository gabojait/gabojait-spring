package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.http.ConflictException;
import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.exception.http.NotFoundException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;

    /**
     * 연락처 저장 |
     * 존재하는 이메일인지 확인하고 이메일에 대한 정보를 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void save(ContactSaveRequestDto request) {
        log.info("INITIALIZE | ContactService | save | " + request.getEmail());
        LocalDateTime initTime = LocalDateTime.now();

        isExistingEmail(request.getEmail());
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
        } catch (Exception e) {
            throw new InternalServerErrorException("연락처 저장 중 에러", e);
        }
    }

    /**
     * 중복 이메일 존재 여부 확인
     * 이메일 중복 여부를 확인한다. 이미 가입된 이메일이면 409(Conflict)를 던지고, 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    private void isExistingEmail(String email) {
        log.info("INITIALIZE | ContactService | isExistingEmail | " + email);
        LocalDateTime initTime = LocalDateTime.now();

        contactRepository.findByEmail(email)
                .ifPresent(contact -> {
                    if (contact.getIsRegistered()) {
                        throw new ConflictException("이미 가입된 이메일입니다");
                    }

                    try {
                        contactRepository.delete(contact);
                    } catch (Exception e) {
                        throw new InternalServerErrorException("중복 이메일 존재 여부 확인 중 에러", e);
                    }
                });

        log.info("COMPLETE | ContactService | isExistingEmail | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + email);
    }

    /**
     * 인증번호 생성 |
     * 숫자, 대문자 영문, 소문자 영문의 6가지 조합을 생성해 반환한다.
     */
    private String generateVerificationCode() {
        log.info("INITIALIZE | ContactService | generateVerificationCode");
        LocalDateTime initTime = LocalDateTime.now();

        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));

        log.info("COMPLETE | ContactService | generateVerificationCode | " +
                Duration.between(initTime, LocalDateTime.now()) + " | " + sb);
        return sb.toString();
    }

    /**
     * 연락처 인증번호 확인 |
     * 이메일로 수신한 인증번호를 확인한다. 존재하지 않은 이메일이면 404(Not Found)을 던지고, 인증번호가 불일치하면 409(Conflict)를 던지고, 서버 에러가
     * 발생하면 500(Internal Server Error)을 던진다.
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

                            log.info("COMPLETE | ContactService | verification | " +
                                    Duration.between(initTime, LocalDateTime.now()) + " | " + contact.getEmail());
                        } catch (Exception e) {
                            throw new InternalServerErrorException("연락처 인증번호 확인 중 에러", e);
                        }
                    } else {
                        throw new ConflictException("인증번호가 틀렸습니다");
                    }
                }, () -> {
                    throw new NotFoundException("존재하지 않은 이메일입니다");
                });
    }

    /**
     * 연락처 단건 조회 |
     * 이메일로 연락처를 조회하고, 존재할 경우 연락처 정보를 반환한다. 연락처가 없을 경우 404(Not Found)를 던지고, 서버 에러가 발생하면
     * 500(Internal Server Error)을 던진다.
     */
    public Contact findOneContact(String email) {
        log.info("INITIALIZE | ContactService | findOneContact | " + email);
        LocalDateTime initTime = LocalDateTime.now();
        Contact contact = contactRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new NotFoundException("인증되지 않은 이메일입니다");
                });
        log.info("COMPLETE | ContactService | findOneContact | " + Duration.between(initTime, LocalDateTime.now())
                + " | " + contact.getEmail());
        return contact;
    }

    /**
     * 유저 가입 완료 |
     * 유저의 연락처에 가입여부 상태를 완료로 바꾸고 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void register(Contact contact) {
        log.info("INITIALIZE | ContactService | register | " + contact.getEmail());
        LocalDateTime initTime = LocalDateTime.now();
        contact.setIsRegistered(true);
        try {
            contactRepository.save(contact);
            log.info("COMPLETE | ContactService | register | " + Duration.between(initTime, LocalDateTime.now()) + " | "
                    + contact.getEmail());
        } catch (Exception e) {
            throw new InternalServerErrorException("유저 가입 완료 중 에러", e);
        }
    }

    /**
     * 연락처 탈퇴 |
     * 연락처에 가입여부 상태를 탈퇴로 바꾸고 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void deactivateContact(Contact contact) {
        log.info("INITIALIZE | ContactService | deactivateContact | " + contact.getEmail());
        LocalDateTime initTime = LocalDateTime.now();
        contact.setIsRegistered(false);
        try {
            contactRepository.save(contact);
            log.info("COMPLETE | ContactService | deactivateContact | " +
                    Duration.between(initTime, LocalDateTime.now()) + " | " + contact.getEmail());
        } catch (Exception e) {
            throw new InternalServerErrorException("연락처 탈퇴 중 에러", e);
        }
    }

    /**
     * 연락처 전체 삭제 |
     * 배포 단계에서 삭제
     */
    public void deleteAll() {
        log.info("INITIALIZE | ContactService | deleteAll");
        LocalDateTime initTime = LocalDateTime.now();
        try {
            contactRepository.deleteAll();
            log.info("COMPLETE  ContactService | deleteAll | " + Duration.between(initTime, LocalDateTime.now()));
        } catch (Exception e) {
            throw new InternalServerErrorException("연락처 전체 삭제 중 에러", e);
        }
    }
}
