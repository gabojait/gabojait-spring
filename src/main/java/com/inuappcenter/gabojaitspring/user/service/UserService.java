package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.exception.http.ConflictException;
import com.inuappcenter.gabojaitspring.exception.http.ForbiddenException;
import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.dto.UserDefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.UserDuplicateRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.UserSaveRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.UserSignInRequestDto;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ContactService contactService;

    private final PasswordEncoder passwordEncoder;

    /**
     * 중복 유저이름 존재 여부 확인 |
     * 중복 유저이름 존재를 파악하고, 이미 사용중인 유저이름이면 409(Conflict)를 던진다. 만약 조회 중 에러가 발생하면 500(Internal Server
     * Error)를 던진다.
     */
    public void isExistingUsername(UserDuplicateRequestDto request) {
        log.info("IN PROGRESS | 중복 유저이름 존재 여부 확인 At " + LocalDateTime.now() + " | " + request.toString());
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    throw new ConflictException("이미 사용중인 아이디입니다");
                });
        log.info("COMPLETE | 중복 유저이름 존재 여부 확인 At " + LocalDateTime.now() + " | " + request);
    }

    /**
     * User 저장 |
     * User의 Contact를 조회한다. 조회되는 Contact의 이메일이 인증이 안됐을 경우 409(Conflict)를 던진다. 이메일 인증을 했을 경우 회원가입을
     * 진행하고 비밀번호는 인코드한다. 만약 유저 정보 저장 중 에러가 발생하면 500(Internal Server Error)를 던진다.
     */
    public UserDefaultResponseDto save(UserSaveRequestDto request) {
        log.info("IN PROGRESS | User 저장 At " + LocalDateTime.now() + " | " + request.toString());
        Contact foundContact = contactService.findOneContact(request.getEmail());
        if (!foundContact.getIsVerified()) {
            throw new ConflictException("이메일 인증을 해주세요");
        }
        contactService.register(foundContact);
        try {
            User roleAssignedUser = assignAsUser(request.toEntity(foundContact));
            roleAssignedUser.setPassword(passwordEncoder.encode(roleAssignedUser.getPassword()));
            User insertedUser = userRepository.insert(roleAssignedUser);
            log.info("COMPLETE | User 저장 At " + LocalDateTime.now() + " | " + insertedUser);
            return new UserDefaultResponseDto(insertedUser);
        } catch (Exception e) {
            throw new InternalServerErrorException("User save 중 에러 발생", e);
        }
    }

    /**
     * 사용자 역할 부여 |
     * User에게 사용자 역할을 부여한다.
     */
    public User assignAsUser(User user) {
        log.info("IN PROGRESS | 사용자 역할 부여 At " + LocalDateTime.now() + " | " + user.toString());
        user.addRole("USER");
        log.info("COMPLETE | 사용자 역할 부여 At " + LocalDateTime.now() + " | " + user);
        return user;
    }

    /**
     * User 로그인 |
     * 아이디를 데이터베이스로 존재 여부를 확인하고 비밀번호가 일치하는지 확인한다. 아이디를 조회해서 없는 경우 또는 아이디는 존재하지만 비밀번호가 틀린
     * 경우 403(Forbidden)을 던진다.
     */
    public UserDefaultResponseDto signIn(UserSignInRequestDto request) {
        log.info("IN PROGRESS | User 로그인 At " + LocalDateTime.now() + " | " + request.toString());
        Optional<User> foundUser = userRepository.findByUsername(request.getUsername());
        if (foundUser.isEmpty()) {
            throw new ForbiddenException("로그인 정보가 틀렸습니다");
        } else
            if (foundUser.get().getPassword().equals(passwordEncoder.encode(request.getPassword()))){
            log.info("COMPLETE | User 로그인 At " + LocalDateTime.now() + " | " + foundUser);
            return new UserDefaultResponseDto(foundUser.get());
        } else {
            throw new ForbiddenException("로그인 정보가 틀렸습니다");
        }
    }

    /**
     * User 전체 삭제 |
     * 배포 단계에서 삭제
     */
    public void deleteAll() {
        try {
            log.info("IN PROGRESS | User 전체 삭제 At " + LocalDateTime.now());
            userRepository.deleteAll();
        } catch (Exception e) {
            throw new InternalServerErrorException("User 전체 삭제 중 에러", e);
        }
        log.info("COMPLETE | User 전체 삭제 At " + LocalDateTime.now());
    }
}
