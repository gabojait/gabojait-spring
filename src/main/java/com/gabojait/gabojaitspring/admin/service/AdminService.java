package com.gabojait.gabojaitspring.admin.service;

import com.gabojait.gabojaitspring.admin.dto.req.AdminLoginReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterReqDto;
import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UtilityProvider utilityProvider;

    /**
     * 관리자 로그인 | main |
     * 401(LOGIN_FAIL)
     */
    public User login(AdminLoginReqDto request) {
        User admin = findOneByUsername(request.getUsername());

        boolean isVerified = utilityProvider.verifyPassword(admin, request.getPassword());
        if (!isVerified)
            throw new CustomException(null, LOGIN_UNAUTHENTICATED);

        return admin;
    }

    /**
     * 가입 | main |
     * 400(PASSWORD_MATCH_INVALID)
     * 409(EXISTING_USERNAME)
     * 500(SERVER_ERROR)
     */
    public User register(AdminRegisterReqDto request) {
        validateDuplicateUsername(request.getAdminName());
        userService.validateMatchingPassword(request.getPassword(), request.getPasswordReEntered());

        String password = utilityProvider.encodePassword(request.getPassword());

        return userService.save(request.toEntity(password));
    }

    /**
     * 아이디로 관리자 단건 조회 |
     * 401(LOGIN_UNAUTHENTICATED)
     */
    private User findOneByUsername(String username) {
        return userRepository.findByUsernameAndRolesInAndIsDeletedIsFalse(username, Role.ADMIN.name())
                .orElseThrow(() -> {
                    throw new CustomException(null, LOGIN_UNAUTHENTICATED);
                });
    }

    /**
     * 중복 아이디 여부 검증 |
     * 409(EXISTING_USERNAME)
     */
    private void validateDuplicateUsername(String username) {
        userRepository.findByUsernameAndRolesIn(username, Role.ADMIN.name())
                .ifPresent(u -> {
                    throw new CustomException(null, EXISTING_USERNAME);
                });
    }
}
