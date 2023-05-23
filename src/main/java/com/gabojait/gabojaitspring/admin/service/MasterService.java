package com.gabojait.gabojaitspring.admin.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class MasterService implements ApplicationRunner {

    @Value("${api.master.id}")
    private String masterUsername;

    private final UserRepository userRepository;
    private final UserService userService;
    private final UtilityProvider utilityProvider;

    /**
     * 마스터 계정 주입 |
     * 500(SERVER_ERROR)
     */
    private void injectMaster() {
        String password = utilityProvider.generateRandomCode(10);
        String encodedPassword = utilityProvider.encodePassword(password);

        User master = User.builder()
                .username(masterUsername)
                .password(encodedPassword)
                .gender(Gender.NONE)
                .roles(List.of(Role.MASTER, Role.ADMIN, Role.USER))
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
            return userRepository.findByUsernameAndRolesIn(masterUsername, List.of(Role.MASTER));
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
