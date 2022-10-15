package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.http.ConflictException;
import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.exception.http.NotFoundException;
import com.inuappcenter.gabojaitspring.exception.http.UnauthorizedException;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.dto.ContactSaveRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactVerificationRequestDto;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
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
        log.info("INITIALIZE | 연락처 저장 At " + LocalDateTime.now() + " | " + request.getEmail());
        isExistingEmail(request.getEmail());
        try {
            Contact contact = contactRepository.save(request.toEntity(generateVerificationCode()));
            emailService.sendEmail(
                    contact.getEmail(),
                    "[가보자it] 인증번호",
                    "회원님 안녕하세요!🙇🏻<br>가입 절차를 계속하기 위해 아래의 번호를 이메일 인증번호란에 입력해주세요.🙏🏻",
                    contact.getVerificationCode()
            );
            log.info("COMPLETE | 연락처 저장 At " + LocalDateTime.now() + " | " + contact.getEmail());
        } catch (Exception e) {
            throw new InternalServerErrorException("연락처 저장 중 에러", e);
        }
    }

    /**
     * 중복 이메일 존재 여부 확인
     * 이메일 중복 여부를 확인한다. 이미 가입된 이메일이면 409(Conflict)를 던지고, 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    private void isExistingEmail(String email) {
        log.info("INITIALIZE | 중복 이메일 존재 여부 확인 At " + LocalDateTime.now() + " | " + email);
        contactRepository.findByEmail(email)
                .ifPresent(contact -> {
                    if (contact.getIsRegistered()) {
                        throw new ConflictException("이미 가입된 이메일입니다");
                    } else {
                        try {
                            contactRepository.delete(contact);
                        } catch (Exception e) {
                            throw new InternalServerErrorException("중복 이메일 존재 여부 확인 중 에러", e);
                        }
                    }
                });
        log.info("COMPLETE | 중복 이메일 존재 여부 확인 At " + LocalDateTime.now() + " | " + email);
    }

    /**
     * 인증번호 생성 |
     * 숫자, 대문자 영문, 소문자 영문의 6가지 조합을 생성해 반환한다.
     */
    private String generateVerificationCode() {
        log.info("INITIALIZE | 인증번호 생성 At " + LocalDateTime.now());
        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        log.info("COMPLETE | 인증번호 생성 At " + LocalDateTime.now());
        return sb.toString();
    }

    /**
     * 연락처 인증번호 확인 |
     * 이메일로 수신한 인증번호를 확인한다. 존재하지 않은 이메일이면 401(Unauthorized)을 던지고, 인증번호가 불일치하면 409(Conflict)를 던지고, 서버
     * 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void verification(ContactVerificationRequestDto request) {
        log.info("INITIALIZE | 연락처 인증번호 확인 At " + LocalDateTime.now() + " | " + request.getEmail());
        contactRepository.findByEmail(request.getEmail())
                .ifPresentOrElse(contact -> {
                    if (contact.getVerificationCode().equals(request.getVerificationCode())) {
                        contact.setIsVerified(true);
                        try {
                            contactRepository.save(contact);
                            log.info("COMPLETE | 연락처 인증번호 확인 At " + LocalDateTime.now() + " | " +
                                    contact.getEmail());
                        } catch (Exception e) {
                            throw new InternalServerErrorException("연락처 인증번호 확인 중 에러", e);
                        }
                    } else {
                        throw new ConflictException("인증번호가 틀렸습니다");
                    }
                }, () -> {
                    throw new UnauthorizedException("존재하지 않은 이메일입니다");
                });
    }

    /**
     * 연락처 단건 조회 |
     * 이메일로 연락처를 조회하고, 존재할 경우 연락처 정보를 반환한다. 연락처가 없을 경우 404(Not Found)를 던지고, 서버 에러가 발생하면
     * 500(Internal Server Error)을 던진다.
     */
    public Contact findOneContact(String email) {
        log.info("INITIALIZE | 연락처 단건 조회 At " + LocalDateTime.now() + " | " + email);
        Optional<Contact> contact = contactRepository.findByEmail(email);
        if (contact.isEmpty()) {
            throw new NotFoundException("인증되지 않은 이메일입니다");
        } else {
            log.info("COMPLETE | 연락처 단건 조회 At " + LocalDateTime.now() + " | " + contact.get().getEmail());
            return contact.get();
        }
    }

    /**
     * 유저 가입 완료 |
     * 유저의 연락처에 가입여부 상태를 완료로 바꾸고 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void register(Contact contact) {
        log.info("INITIALIZE | 유저 가입 완료 At " + LocalDateTime.now() + " | " + contact.getEmail());
        contact.setIsRegistered(true);
        try {
            contactRepository.save(contact);
            log.info("COMPLETE | 유저 가입 완료 At " + LocalDateTime.now() + " | " + contact.getEmail());
        } catch (Exception e) {
            throw new InternalServerErrorException("유저 가입 완료 중 에러", e);
        }
    }

    /**
     * 연락처 탈퇴 |
     * 연락처에 가입여부 상태를 탈퇴로 바꾸고 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void deactivateContact(Contact contact) {
        log.info("INITIALIZE | 연락처 탈퇴 At " + LocalDateTime.now() + " | " + contact.getEmail());
        contact.setIsRegistered(false);
        try {
            contactRepository.save(contact);
            log.info("COMPLETE | 연락처 탈퇴 At " + LocalDateTime.now() + " | " + contact.getEmail());
        } catch (Exception e) {
            throw new InternalServerErrorException("연락처 탈퇴 중 에러", e);
        }
    }

    /**
     * 연락처 전체 삭제 |
     * 배포 단계에서 삭제
     */
    public void deleteAll() {
        log.info("INITIALIZE | 연락처 전체 삭제 At " + LocalDateTime.now());
        try {
            contactRepository.deleteAll();
            log.info("COMPLETE | 연락처 전체 삭제 At " + LocalDateTime.now());
        } catch (Exception e) {
            throw new InternalServerErrorException("연락처 전체 삭제 중 에러", e);
        }
    }
}
