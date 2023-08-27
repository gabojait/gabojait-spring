package com.gabojait.gabojaitspring.user.service;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.common.util.PasswordProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.Admin;
import com.gabojait.gabojaitspring.user.domain.UserRole;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.AdminRepository;
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

import java.time.LocalDate;
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

    private final AdminRepository adminRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordProvider passwordProvider;
    private final PageProvider pageProvider;

    /**
     * 관리자 가입 결정 |
     * 404(ADMIN_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void decideAdminRegistration(Long adminId, boolean isApproval) {
        Admin admin = findOneUnregisteredAdmin(adminId);

        if (isApproval) {
            admin.decideRegistration(true);
        } else {
            admin.decideRegistration(false);
        }

        saveAdmin(admin);
    }

    /**
     * 마스터 계정 비밀번호 리셋 스케줄러 |
     * 500(SERVER_ERROR)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void resetMasterPasswordScheduler() {
        String password = passwordProvider.generateRandomCode(10);
        String encodedPassword = passwordProvider.encodePassword(password);

        Optional<Admin> master = findOneMaster();
        if (master.isPresent()) {
            master.get().updatePassword(encodedPassword);
        } else {
            injectMaster();
        }

        masterLogging(password, true);
    }

    /**
     * 관리자 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveAdmin(Admin admin) {
        try {
            adminRepository.save(admin);
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
    public Page<Admin> findManyUnregisteredAdmin(long pageFrom, Integer pageSize) {
        pageFrom = pageProvider.validatePageFrom(pageFrom);
        Pageable pageable = pageProvider.validatePageable(pageSize, 5);

        try {
            return adminRepository.findAllByIdIsLessThanAndIsApprovedIsNullAndIsDeletedIsFalseOrderByCreatedAtDesc(pageFrom, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 마스터 계정 조회 |
     * 500(SERVER_ERROR)
     */
    private Optional<Admin> findOneMaster() {
        try {
            return adminRepository.findByUsernameAndIsApprovedIsTrueAndIsDeletedIsFalse(masterName);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 식별자로 가입 대기 관리자 단건 조회 |
     * 404(ADMIN_NOT_FOUND)
     */
    private Admin findOneUnregisteredAdmin(Long adminId) {
        return adminRepository.findByIdAndIsApprovedIsNullAndIsDeletedIsFalse(adminId)
                .orElseThrow(() -> {
                    throw new CustomException(ADMIN_NOT_FOUND);
                });
    }

    /**
     * 마스터 권한 생성
     */
    private List<UserRole> createMasterRoles(Admin master) {
        List<UserRole> masterRoles = new ArrayList<>();

        masterRoles.add(UserRole.builder()
                .admin(master)
                .role(Role.USER)
                .build());
        masterRoles.add(UserRole.builder()
                .admin(master)
                .role(Role.ADMIN)
                .build());
        masterRoles.add(UserRole.builder()
                .admin(master)
                .role(Role.MASTER)
                .build());

        return masterRoles;
    }

    /**
     * 마스터 계정 주입 |
     * 500(SERVER_ERROR)
     */
    private void injectMaster() {
        String password = passwordProvider.generateRandomCode(10);
        String encodedPassword = passwordProvider.encodePassword(password);

        Admin master = Admin.builder()
                .username(masterName)
                .password(encodedPassword)
                .birthdate(LocalDate.of(1997, 2, 11))
                .legalName(masterName)
                .build();
        master.decideRegistration(true);
        saveAdmin(master);

        List<UserRole> masterRoles = createMasterRoles(master);
        saveMasterRole(masterRoles);

        masterLogging(password, false);
    }

    /**
     * 마스터 로깅
     */
    private void masterLogging(String password, boolean isScheduled) {
        String title = isScheduled ? "SCHEDULED" : "RUNNER";

        log.info("========== [{} | SUCCESS] master password = {} ==========", title, password);
    }

    @Override
    public void run(ApplicationArguments args) {
        Optional<Admin> master = findOneMaster();

        if (master.isEmpty())
            injectMaster();
    }
}
