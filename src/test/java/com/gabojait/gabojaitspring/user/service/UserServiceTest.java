package com.gabojait.gabojaitspring.user.service;

import com.gabojait.gabojaitspring.common.util.EmailProvider;
import com.gabojait.gabojaitspring.common.util.FileProvider;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.fcm.repository.FcmRepository;
import com.gabojait.gabojaitspring.user.repository.ContactRepository;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private FcmRepository fcmRepository;

    @Mock
    private GeneralProvider generalProvider;

    @Mock
    private EmailProvider emailProvider;

    @Mock
    private FileProvider fileProvider;

    @InjectMocks
    private UserService userService;

    @Test
    void validateUsernameO() {
        // Test case 1: Validate username
        String username1 = "tester";
        assertDoesNotThrow(() -> userService.validateUsername(username1));

        // Test case 2: Validate username
//        String username2 = "";
    }
}