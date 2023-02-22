package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.user.domain.type.Gender;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.user.dto.req.UserLoginReqDto;
import com.inuappcenter.gabojaitspring.user.dto.req.UserSaveReqDto;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 중복 아이디 여부 확인 |
     * 409(EXISTING_USERNAME)
     */
    public void isExistingUsername(String username) {

        userRepository.findByUsernameAndIsDeletedIsFalse(username)
                .ifPresent(u -> {
                    throw new CustomException(EXISTING_USERNAME);
                });
    }

    /**
     * 성별 검증 |
     * 400(GENDER_FORMAT_INVALID)
     */
    public Gender validateGender(Character gender) {

        if (gender == Gender.MALE.getType()) {
            return Gender.MALE;
        } else if (gender == Gender.FEMALE.getType()) {
            return Gender.FEMALE;
        } else {
            throw new CustomException(GENDER_FORMAT_INVALID);
        }
    }

    /**
     * 비밀번호와 비밀번호 재입력 검증과 인코딩 |
     * 400(PASSWORD_MATCH_INVALID)
     */
    public String validatePwAndPwReEnterAndEncode(String password, String passwordReEntered) {

        if (!password.equals(passwordReEntered))
            throw new CustomException(PASSWORD_MATCH_INVALID);
        else
            return passwordEncoder.encode(password);
    }

    /**
     * 유저 저장 |
     * 500(SERVER_ERROR)
     */
    public User save(User user) {

        try {
            return userRepository.save(user);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }


    /**
     * 유저 생성 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public User create(UserSaveReqDto request, String encodedPassword, Gender gender, Contact contact) {

        List<Role> roles = new ArrayList<>();

        if (true) { // 유저 가입
            roles.add(Role.USER);
        } else { // 관리자 가입
            roles.add(Role.USER);
            roles.add(Role.ADMIN);
        }
        User user = request.toEntity(encodedPassword, gender, contact, roles);

        return save(user);
    }

    /**
     * 로그인
     * 401(LOGIN_FAIL)
     */
    public User login(UserLoginReqDto request) {

        User user = userRepository.findByUsernameAndIsDeletedIsFalse(request.getUsername())
                .orElseThrow(() -> {
                    throw new CustomException(LOGIN_FAIL);
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new CustomException(LOGIN_FAIL);

        return user;
    }

    /**
     * 식별자 단건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public User findOneByUserId(String userId) {

        try {
            return userRepository.findByIdAndIsDeletedIsFalse(new ObjectId(userId))
                    .orElseThrow(() -> {
                        throw new CustomException(USER_NOT_FOUND);
                    });
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 중복 닉네임 여부 확인 |
     * 409(EXISTING_NICKNAME)
     */
    public void isExistingNickname(String nickname) {

        userRepository.findByNicknameAndIsDeletedIsFalse(nickname)
                .ifPresent(user -> {
                    throw new CustomException(EXISTING_NICKNAME);
                });
    }

    /**
     * 닉네임 업데이트 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public User updateNickname(User user, String nickname) {

        try {
            user.updateNickname(nickname);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        return user;
    }

    /**
     * 연락처 단건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public User findOneByContact(Contact contact) {

        try {
            return userRepository.findByContactAndIsDeletedIsFalse(contact)
                    .orElseThrow(() -> {
                        throw new CustomException(USER_NOT_FOUND);
                    });
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 아이디 이메일로 전송 |
     */
    public void sendUsernameEmail(User user) {
        emailService.sendEmail(
                user.getContact().getEmail(),
                "[가보자잇] 아이디 찾기",
                user.getLegalName() + "님 안녕하세요!🙇🏻<br>해당 이메일로 가입된 아이디 정보입니다.",
                user.getUsername()
        );
    }

    /**
     * 아이디 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    public User findOneByUsername(String username) {

        return userRepository.findByUsernameAndIsDeletedIsFalse(username)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 임시 비밀번호 발급 후 이메일로 전송 |
     * 401(USERNAME_EMAIL_NO_MATCH)
     */
    @Transactional
    public void resetPasswordAndSendEmail(User user, String email) {

        if (email.equals(user.getContact().getEmail())) {
            String tempPassword = generateTemporaryPassword();
            user.updatePassword(passwordEncoder.encode(tempPassword));
            user.updateIsTemporaryPassword(true);

            emailService.sendEmail(
                    user.getContact().getEmail(),
                    "[가보자잇] 비밀번호 찾기",
                    user.getLegalName() +
                            "님 안녕하세요!🙇🏻<br>임시 비밀번호를 제공해 드립니다.<br>접속 후 비밀번호를 변경 해주세요.",
                    tempPassword
            );
        } else {
            throw new CustomException(USERNAME_EMAIL_NO_MATCH);
        }
    }

    /**
     * 임시 비밀번호 생성 |
     */
    private String generateTemporaryPassword() {
        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));

        return sb.toString();
    }

    /**
     * 현재 비밀번호 검증 |
     * 401(PASSWORD_AUTHENTICATION_FAIL)
     */
    public void validatePassword(String encodedPassword, String password) {
        if (!passwordEncoder.matches(password, encodedPassword))
            throw new CustomException(PASSWORD_AUTHENTICATION_FAIL);
    }

    /**
     * 비밀번호 업데이트 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void updatePassword(User user, String encodedPassword, boolean isTemporaryPassword) {

        try {
            if (isTemporaryPassword)
                user.updateIsTemporaryPassword(false);

            user.updatePassword(encodedPassword);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 탈퇴 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void deactivate(User user) {
        try {
            user.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 학력 추가 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void addEducation(User user, Education education) {

        try {
            user.addEducation(education);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 학력 제거 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void removeEducation(User user, Education education) {

        try {
            user.removeEducation(education);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 경력 추가 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void addWork(User user, Work work) {

        try {
            user.addWork(work);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 경력 제거 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void removeWork(User user, Work work) {

        try {
            user.removeWork(work);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 기술 추가 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void addSkill(User user, Skill skill) {

        try {
            user.addSkill(skill);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 기술 제거 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void removeSkill(User user, Skill skill) {

        try {
            user.removeSkill(skill);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 추가 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void addPortfolio(User user, Portfolio portfolio) {

        try {
            user.addPortfolio(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 제거 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void removePortfolio(User user, Portfolio portfolio) {

        try {
            user.removePortfolio(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
