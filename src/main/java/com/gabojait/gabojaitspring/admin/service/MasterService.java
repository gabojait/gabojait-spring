package com.gabojait.gabojaitspring.admin.service;

import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.UserRole;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MasterService implements ApplicationRunner {

    @Value("${api.master.id}")
    private String masterName;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final GeneralProvider generalProvider;

    /**
     * 관리자 가입 결정 |
     * 404(ADMIN_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void decideAdminRegistration(Long adminId, boolean isApproval) {
        User admin = findOneUnregisteredAdmin(adminId);

        if (isApproval) {
            admin.decideAdminRegistration(null);
        } else {
            admin.decideAdminRegistration(true);
        }

        saveAdmin(admin);
    }

    /**
     * 마스터 계정 비밀번호 리셋 스케줄러 |
     * 500(SERVER_ERROR)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void resetMasterPasswordScheduler() {
        String password = generalProvider.generateRandomCode(10);
        String encodedPassword = generalProvider.encodePassword(password);

        Optional<User> master = findOneMaster();
        if (master.isPresent()) {
            master.get().updatePassword(encodedPassword, false);
        } else {
            injectMaster();
        }

        masterLogging("[MASTER PASSWORD RESET]", password);
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
     * 마스터 권한 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveMasterRole(List<UserRole> masterRoles) {
        try {
            userRoleRepository.saveAll(masterRoles);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 가입 승인을 기다리는 관리자 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    public Page<User> findManyUnregisteredAdmin(Integer pageFrom, Integer pageSize) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 5);

        try {
            return userRepository.findAllByUsernameEndsWithAndIsDeletedIsTrue("_admin", pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 마스터 계정 조회 |
     * 500(SERVER_ERROR)
     */
    private Optional<User> findOneMaster() {
        try {
            return userRepository.findByUsername(masterName);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 식별자로 가입 대기 관리자 단건 조회 |
     * 404(ADMIN_NOT_FOUND)
     */
    private User findOneUnregisteredAdmin(Long adminId) {
        return userRepository.findByIdAndIsDeletedIsTrue(adminId)
                .orElseThrow(() -> {
                    throw new CustomException(ADMIN_NOT_FOUND);
                });
    }

    /**
     * 마스터 권한 생성
     */
    private List<UserRole> createMasterRoles(User master) {
        List<UserRole> masterRoles = new ArrayList<>();

        masterRoles.add(UserRole.builder()
                .user(master)
                .role(Role.USER)
                .build());
        masterRoles.add(UserRole.builder()
                .user(master)
                .role(Role.ADMIN)
                .build());
        masterRoles.add(UserRole.builder()
                .user(master)
                .role(Role.MASTER)
                .build());

        return masterRoles;
    }

    /**
     * 마스터 계정 주입 |
     * 500(SERVER_ERROR)
     */
    private void injectMaster() {
        String password = generalProvider.generateRandomCode(10);
        String encodedPassword = generalProvider.encodePassword(password);

        User master = User.masterBuilder()
                .username(masterName)
                .password(encodedPassword)
                .build();
        saveAdmin(master);

        List<UserRole> masterRoles = createMasterRoles(master);
        saveMasterRole(masterRoles);

        masterLogging("[NEW MASTER]", password);
    }

    /**
     * 마스터 로깅
     */
    private void masterLogging(String title, String password) {
        int logLength = 50;
        String logTitle = "=".repeat((logLength - title.length()) / 2)
                .concat(title)
                .concat("=".repeat((logLength - title.length()) / 2));
        String logMessage = "=".repeat((logLength - password.length()) / 2)
                .concat(password)
                .concat("=".repeat((logLength - password.length()) / 2));
        String logSpace = "=".repeat(logLength);

        log.info(logSpace);
        log.info(logTitle);
        log.info(logMessage);
        log.info(logSpace);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<User> master = findOneMaster();

        if (master.isEmpty())
            injectMaster();
    }
}
