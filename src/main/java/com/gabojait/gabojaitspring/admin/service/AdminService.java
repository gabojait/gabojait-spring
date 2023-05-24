package com.gabojait.gabojaitspring.admin.service;

import com.gabojait.gabojaitspring.admin.dto.req.AdminLoginReqDto;
import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
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
     * 아이디로 관리자 단건 조회 |
     * 401(LOGIN_UNAUTHENTICATED)
     */
    private User findOneByUsername(String username) {
        return userRepository.findByUsernameAndRolesInAndIsDeletedIsFalse(username, Role.ADMIN.name())
                .orElseThrow(() -> {
                    throw new CustomException(null, LOGIN_UNAUTHENTICATED);
                });
    }
}
