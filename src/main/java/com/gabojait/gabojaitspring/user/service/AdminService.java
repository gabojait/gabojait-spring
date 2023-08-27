package com.gabojait.gabojaitspring.user.service;

import com.gabojait.gabojaitspring.user.domain.Admin;
import com.gabojait.gabojaitspring.user.dto.req.AdminLoginReqDto;
import com.gabojait.gabojaitspring.user.dto.req.AdminRegisterReqDto;
import com.gabojait.gabojaitspring.common.util.PasswordProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.UserRole;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.AdminRepository;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordProvider passwordProvider;

    /**
     * 관리자 로그인 |
     * 401(LOGIN_UNAUTHENTICATED)
     * 500(SERVER_ERROR)
     */
    public Admin login(AdminLoginReqDto request) {
        Admin admin = findOneAdmin(request.getUsername());

        boolean isVerified = passwordProvider.verifyPassword(admin, request.getPassword());
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
        validateDuplicateUsername(request.getUsername());
        validateMatchingPassword(request.getPassword(), request.getPasswordReEntered());

        String password = passwordProvider.encodePassword(request.getPassword());

        Admin admin = saveAdmin(request.toEntity(password));
        List<UserRole> adminRoles = createAdminRoles(admin);
        saveAdminRoles(adminRoles);
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    public User findOneUser(Long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 관리자 저장 |
     * 500(SERVER_ERROR)
     */
    private Admin saveAdmin(Admin admin) {
        try {
            return adminRepository.save(admin);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 관리자 권한 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveAdminRoles(List<UserRole> adminRoles) {
        try {
            userRoleRepository.saveAll(adminRoles);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 관리자 권한 생성
     */
    private List<UserRole> createAdminRoles(Admin admin) {
        List<UserRole> adminRoles = new ArrayList<>();

        adminRoles.add(UserRole.builder()
                .admin(admin)
                .role(Role.USER)
                .build());
        adminRoles.add(UserRole.builder()
                .admin(admin)
                .role(Role.ADMIN)
                .build());

        return adminRoles;
    }

    /**
     * 아이디로 관리자 단건 조회 |
     * 401(LOGIN_UNAUTHENTICATED)
     */
    private Admin findOneAdmin(String username) {
        Optional<Admin> admin = adminRepository.findByUsernameAndIsApprovedIsTrueAndIsDeletedIsFalse(username);

        if (admin.isEmpty())
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        for (UserRole userRole : admin.get().getUserRoles())
            if (userRole.getRole().equals(Role.ADMIN))
                return admin.get();

        throw new CustomException(LOGIN_UNAUTHENTICATED);
    }

    /**
     * 마지막 요청일 업데이트 |
     * 500(SERVER_ERROR)
     */
    private void updateLastRequestAt(Admin admin) {
        admin.updateLastRequestAt();
    }

    /**
     * 중복 아이디 여부 검증 |
     * 409(EXISTING_USERNAME)
     */
    private void validateDuplicateUsername(String username) {
        Optional<Admin> admin = adminRepository.findByUsernameAndIsDeletedIsFalse(username);

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
