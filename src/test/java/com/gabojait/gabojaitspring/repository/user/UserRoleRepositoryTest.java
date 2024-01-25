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
    @DisplayName("회원 아이디로 전체 권한 조회가 정상 작동한다")
    void givenUsername_whenFindAll_thenReturn() {
        // given
        User user = createSavedDefaultUser();
        Role role = Role.USER;
        UserRole userRole = createUserRole(user, role);
        userRoleRepository.save(userRole);

        // when
        List<UserRole> userRoles = userRoleRepository.findAll(user.getUsername());

        // then
        assertAll(
                () -> assertThat(userRoles).containsExactly(userRole),
                () -> assertThat(userRoles.size()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("회원 식별자로 전체 권한 조회가 정상 작동한다")
    void givenUserId_findAll_thenReturn() {
        // given
        User user = createSavedDefaultUser();
        Role role = Role.USER;
        UserRole userRole = createUserRole(user, role);
        userRoleRepository.save(userRole);

        // when
        List<UserRole> userRoles = userRoleRepository.findAll(user.getId());

        // then
        assertAll(
                () -> assertThat(userRoles)
                        .containsExactlyInAnyOrder(userRole),
                () -> assertThat(userRoles).extracting("user")
                        .containsExactlyInAnyOrder(user)
        );
    }

    private UserRole createUserRole(User user, Role role) {
        return UserRole.builder()
                .role(role)
                .user(user)
                .build();
    }

    private User createSavedDefaultUser() {
        Contact contact = Contact.builder()
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .build();
        contact.verified();
        contactRepository.save(contact);

        User user = User.builder()
                .username("tester")
                .password("password1!")
                .nickname("테스터")
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