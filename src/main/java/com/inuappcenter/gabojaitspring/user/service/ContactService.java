package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.http.ConflictException;
import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.exception.http.NotFoundException;
import com.inuappcenter.gabojaitspring.exception.http.UnauthorizedException;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.dto.ContactDefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactSaveRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactVerificationRequestDto;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Contact 저장 |
     * 존재하는 이메일인지 확인후에 존재하지 않으면 이메일에 대한 정보를 저장한다. 저장 중 오류가 발생하면 500(Internal Server Error)을 던진다.
     */
    public ContactDefaultResponseDto save(ContactSaveRequestDto request) {
        log.info("IN PROGRESS | Contact 저장 At " + LocalDateTime.now() + " | " + request.toString());
        isExistingEmail(request.getEmail());
        try {
            Contact insertedContact = contactRepository.insert(request.toEntity(generateVerificationCode()));
            emailService.sendEmail(
                    insertedContact.getEmail(),
                    "가보자it 인증번호",
                    "회원님 안녕하세요!🙇🏻<br>가입 절차를 계속하기 위해 아래의 번호를 이메일 인증번호란에 입력해주세요.🙏🏻",
                    insertedContact.getVerificationCode()
            );
            log.info("COMPLETE | 중복 이메일 존재 여부 확인 At " + LocalDateTime.now() + " | " + insertedContact);
            return new ContactDefaultResponseDto(insertedContact);
        } catch (Exception e) {
            throw new InternalServerErrorException("Contact 저장 중 에러", e);
        }
    }

    /**
     * 중복 이메일 존재 여부 확인
     * 이미 가입된 이메일이면 409(Conflict)를 던진다. 가입되지 않은 이메일의 정보가 있다면 이메일을 삭제하고, 삭제 중 오류가 발생하면 500(Internal
     * Server Error)를 던진다.
     */
    private void isExistingEmail(String email) {
        log.info("IN PROGRESS | 중복 이메일 존재 여부 확인 At " + LocalDateTime.now() + " | " + email);
        Optional<Contact> existingContact = contactRepository.findByEmail(email);
        if (existingContact.isPresent()) {
            if (existingContact.get().getIsRegistered()) {
                throw new ConflictException("이미 가입된 이메일입니다");
            }
            try {
                contactRepository.delete(existingContact.get());
            } catch (Exception e) {
                throw new InternalServerErrorException("이메일 중복 여부 중 에러 발생", e);
            }
        }
        log.info("COMPLETE | 중복 이메일 존재 여부 확인 At " + LocalDateTime.now() + " | " + email);
    }

    /**
     * 인증번호 생성 |
     * 숫자, 대문자 영문, 소문자 영문의 6가지 조합을 생성해 반환한다.
     */
    private String generateVerificationCode() {
        log.info("IN PROGRESS | 인증번호 생성 At " + LocalDateTime.now());
        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        log.info("COMPLETE | 인증번호 생성 At " + LocalDateTime.now() + " | " + sb);
        return sb.toString();
    }

    /**
     * 인증번호 확인 후 Contact 업데이트 |
     * 인증 요청이 안된 이메일이면 401(Unauthorized)를 던진다. 인증번호 확인 후에 불일치하다면 401(Unauthorized)를 던진다.
     */
    public ContactDefaultResponseDto update(ContactVerificationRequestDto request) {
        log.info("IN PROGRESS | 인증번호 확인 후 Contact 업데이트 At " + LocalDateTime.now() + " | " + request.toString());
        Optional<Contact> foundContact = contactRepository.findByEmail(request.getEmail());
        if (foundContact.isEmpty()) {
            throw new UnauthorizedException("인증되지 않은 이메일입니다");
        }

        if (foundContact.get().getVerificationCode().equals(request.getVerificationCode())) {
            try {
                foundContact.get().setIsVerified(true);
                Contact savedContact = contactRepository.save(foundContact.get());
                log.info("COMPLETE | 인증번호 확인 완료 Contact 업데이트 At " + LocalDateTime.now() + " | " + savedContact);
                return new ContactDefaultResponseDto(savedContact);
            } catch (Exception e) {
                throw new InternalServerErrorException("인증번호 확인 후 Contact 업데이트 중 에러", e);
            }
        } else {
            throw new UnauthorizedException("인증번호가 틀렸습니다");
        }
    }

    /**
     * Contact 단건 조회 |
     * 이메일로 Contact를 조회하고, 존재할 경우 Contact 정보를 반환한다. Contact가 없을 경우 404(Not Found)를 던진다. 이메일로 Contact 조회
     * 중 에러가 발생하면 500(Internal Server Error)를 던진다.
     */
    public Contact findOneContact(String email) {
        log.info("IN PROGRESS | Contact 단건 조회 At " + LocalDateTime.now() + " | " + email);
        Optional<Contact> contact = contactRepository.findByEmail(email);
        if (contact.isEmpty()) {
            throw new NotFoundException("인증되지 않은 이메일입니다");
        } else {
            log.info("COMPLETE | Contact 단건 조회 At " + LocalDateTime.now() + " | " + contact);
            return contact.get();
        }
    }

    /**
     * User 가입 완료 |
     * User의 Contact에 가입여부를 true로 바꾸고 저장한다. 저장 중 에러가 발생하면 500(Internal Server Error)를 던진다.
     */
    public void register(Contact contact) {
        log.info("IN PROGRESS | User 가입 완료 At " + LocalDateTime.now() + " | " + contact.toString());
        contact.setIsRegistered(true);
        try {
            contactRepository.save(contact);
            log.info("COMPLETE | Contact 단건 조회 At " + LocalDateTime.now() + " | " + contact);
        } catch (Exception e) {
            throw new InternalServerErrorException("User 가입 완료 중 에러", e);
        }
    }

    /**
     * Contact 전체 삭제 |
     * 배포 단계에서 삭제
     */
    public void deleteAll() {
        log.info("IN PROGRESS | Contact 전체 삭제 At " + LocalDateTime.now());
        try {
            contactRepository.deleteAll();
            log.info("COMPLETE | Contact 전체 삭제 At " + LocalDateTime.now());
        } catch (Exception e) {
            throw new InternalServerErrorException("Contact 전체 삭제 중 에러", e);
        }
    }

}
