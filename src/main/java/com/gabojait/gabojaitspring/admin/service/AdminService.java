package com.gabojait.gabojaitspring.admin.service;

import com.gabojait.gabojaitspring.admin.dto.req.AdminLoginReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterReqDto;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final GeneralProvider generalProvider;

    /**
     * 관리자 로그인 |
     * 401(LOGIN_UNAUTHENTICATED)
     * 500(SERVER_ERROR)
     */
    public User login(AdminLoginReqDto request) {
        User admin = findOneAdmin(request.getUsername());

        boolean isVerified = generalProvider.verifyPassword(admin, request.getPassword());
        if (!isVerified)
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        updateLastRequestAt(admin);

        return admin;
    }

    /**
     * 관리자 가입 |
     * 400(PASSWORD_MATCH_INVALID)
     * 409(EXISTING_USERNAME)
     * 500(SERVER_ERROR)
     */
    public void register(AdminRegisterReqDto request) {
        validateDuplicateUsername(request.getAdminName());
        validateMatchingPassword(request.getPassword(), request.getPasswordReEntered());

        String password = generalProvider.encodePassword(request.getPassword());

        saveAdmin(request.toEntity(password));
    }

    /**
     * 관리자 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveAdmin(User admin) {
        try {
            userRepository.save(admin);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 아이디로 관리자 단건 조회 |
     * 401(LOGIN_UNAUTHENTICATED)
     */
    private User findOneAdmin(String username) {
        Optional<User> admin = userRepository.findByUsernameAndIsDeletedIsNull(username);

        if (admin.isEmpty())
            throw new CustomException(LOGIN_UNAUTHENTICATED);
        if (!admin.get().getRoles().contains(Role.ADMIN.name()))
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        return admin.get();
    }

    /**
     * 마지막 요청일 업데이트 |
     * 500(SERVER_ERROR)
     */
    private void updateLastRequestAt(User user) {
        user.updateLastRequestAt();
    }

    /**
     * 중복 아이디 여부 검증 |
     * 409(EXISTING_USERNAME)
     */
    private void validateDuplicateUsername(String username) {
        Optional<User> admin = userRepository.findByUsername(username);

        if (admin.isPresent())
            throw new CustomException(EXISTING_USERNAME);
    }

    /**
     * 비밀번호와 비밀번호 재입력 검증 |
     * 400(PASSWORD_MATCH_INVALID)
     */
    private void validateMatchingPassword(String password, String passwordReEntered) {
        if (!password.equals(passwordReEntered))
            throw new CustomException(PASSWORD_MATCH_INVALID);
    }
}
