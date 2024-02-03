package com.gabojait.gabojaitspring.api.service.user;

import com.gabojait.gabojaitspring.common.util.PasswordUtility;
import com.gabojait.gabojaitspring.domain.user.*;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import com.gabojait.gabojaitspring.repository.user.UserRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class UserDetailsServiceTest {

    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordUtility passwordUtility;

    @Test
    @DisplayName("회원 식별자로 회원 상세 정보를 조회한다")
    void givenValid_whenLoadUserByUserId_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);
        Role role1 = Role.USER;
        UserRole userRole1 = createUserRole(user, role1);
        Role role2 = Role.ADMIN;
        UserRole userRole2 = createUserRole(user, role2);
        userRoleRepository.saveAll(List.of(userRole1, userRole2));

        // when
        UserDetails userDetails = userDetailsService.findUserDetails(user.getId());

        // then
        assertAll(
                () -> assertThat(userDetails)
                        .extracting("username", "password")
                        .containsExactly(user.getUsername(), user.getPassword()),
                () -> assertThat(userDetails.getAuthorities())
                        .extracting("authority")
                        .containsExactlyInAnyOrder(role1.name(), role2.name())
        );
    }

    private UserRole createUserRole(User user, Role role) {
        return UserRole.builder()
                .user(user)
                .role(role)
                .build();
    }

    @Test
    @DisplayName("존재하지 않은 회원 식별자로 회원 상세 정보를 조회하면 예외가 발생한다.")
    void givenNonExistingUser_whenLoadUserByUserId_thenThrow() {
        // given
        long userId = 1L;

        // when & then
        assertThatThrownBy(() -> userDetailsService.findUserDetails(userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    private Contact createContact(String email) {
        return Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
    }

    private User createUser(String username, String nickname, Contact contact) {
        return User.builder()
                .username(username)
                .password(passwordUtility.encodePassword("password1!"))
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(LocalDateTime.now())
                .contact(contact)
                .build();
    }
}
