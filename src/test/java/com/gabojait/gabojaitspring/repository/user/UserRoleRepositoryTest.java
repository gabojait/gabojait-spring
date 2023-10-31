package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.domain.user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserRoleRepositoryTest {

    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;

    @Test
    @DisplayName("회원 아이디로 전체 권한 조회를 한다.")
    void givenValid_whenFindAll_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Role role = Role.USER;
        UserRole userRole = createUserRole(user, role);
        userRoleRepository.save(userRole);

        // when
        List<UserRole> userRoles = userRoleRepository.findAll(user.getUsername());

        // then
        assertAll(
                () -> assertThat(userRoles)
                        .extracting("role")
                        .containsExactly(Role.USER),
                () -> assertThat(userRoles.size()).isEqualTo(1),
                () -> assertThat(userRoles)
                        .extracting("id", "createdAt", "updatedAt")
                        .isNotNull()
        );
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 전체 권한 조회를 한다.")
    void givenNonExistingUsername_whenFindAll_thenReturn() {
        // given
        String username = "tester";

        // when
        List<UserRole> userRoles = userRoleRepository.findAll(username);

        // then
        assertThat(userRoles).isEmpty();
    }

    private UserRole createUserRole(User user, Role role) {
        return UserRole.builder()
                .role(role)
                .user(user)
                .build();
    }

    private User createSavedDefaultUser(String email, String username, String nickname) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
        contact.verified();
        contactRepository.save(contact);

        User user = User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(LocalDateTime.now())
                .contact(contact)
                .build();
        user.updatePosition(Position.BACKEND);
        userRepository.save(user);

        return user;
    }
}