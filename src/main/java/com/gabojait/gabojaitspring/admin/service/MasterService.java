package com.gabojait.gabojaitspring.admin.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterService implements ApplicationRunner {

    @Value("${api.master.id}")
    private String masterUsername;

    private final UserRepository userRepository;
    private final UserService userService;
    private final UtilityProvider utilityProvider;

    /**
     * 가입 승인을 기다리는 관리자 페이징 조회 | main |
     * 500(SERVER_ERROR)
     */
    public Page<User> findPageUnregisteredAdmin(Integer pageFrom, Integer pageSize) {
        Pageable pageable = utilityProvider.validatePaging(pageFrom, pageSize, 5);

        try {
            return userRepository.findAllByRolesInAndIsDeletedNotExists(Role.ADMIN.name(), pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 관리자 가입 결정 | main |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void decideAdminRegistration(String adminId, boolean isApproval) {
        User admin = findOneById(adminId);

        admin.approveAdminRegistration(!isApproval);

        userService.save(admin);
    }

    /**
     * 회원 식별자 미가입 관리자 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneById(String adminId) {
        ObjectId id = utilityProvider.toObjectId(adminId);

        return userRepository.findByIdAndRolesInAndIsDeletedNotExists(id, Role.ADMIN.name())
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 마스터 계정 주입 |
     * 500(SERVER_ERROR)
     */
    private void injectMaster() {
        String password = utilityProvider.generateRandomCode(10);
        String encodedPassword = utilityProvider.encodePassword(password);

        User master = User.masterBuilder()
                .username(masterUsername)
                .password(encodedPassword)
                .build();

        userService.save(master);

        masterLogging("[NEW MASTER]", password);
    }

    /**
     * 마스터 계정 조회 |
     * 500(SERVER_ERROR)
     */
    private Optional<User> findOneMaster() {
        try {
            return userRepository.findByUsernameAndRolesInAndIsDeletedIsFalse(masterUsername, Role.MASTER.name());
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 마스터 계정 비밀번호 리셋 스케줄러 |
     * 500(SERVER_ERROR)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void resetMasterPasswordScheduler() {
        String password = utilityProvider.generateRandomCode(10);

        Optional<User> master = findOneMaster();
        if (master.isPresent()) {
            userService.updatePassword(master.get(), password, password, false);
        } else {
            injectMaster();
        }

        masterLogging("[MASTER PASSWORD RESET]", password);
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
