package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;

    @Test
    @DisplayName("연락처로 회원 단건 조회가 정상 작동한다")
    void givenValid_whenFindByContact_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when
        User foundUser = userRepository.findByContact(contact).get();

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    @DisplayName("아이디로 회원 단건 조회가 정상 작동한다")
    void givenValid_whenFindByUsername_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when
        User foundUser = userRepository.findByUsername(user.getUsername()).get();

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    @DisplayName("닉네임으로 회원 단건 조회가 정상 작동한다")
    void givenValid_whenFindByNickname_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when
        User foundUser = userRepository.findByNickname(user.getNickname()).get();

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    @DisplayName("존재하는 회원 아이디로 회원 존재 여부 조회시 참을 반환한다")
    void givenExisting_whenExistsByUsername_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByUsername(user.getUsername());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 회원 식별자로 회원 존재 여부 조회시 거짓을 반환한다")
    void givenNonExisting_whenExistsByUsername_thenReturn() {
        // given
        String username = "tester";

        // when
        boolean result = userRepository.existsByUsername(username);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("존재하는 회원 닉네임으로 회원 존재 여부 조회시 참을 반환한다")
    void givenExisting_whenExistsByNickname_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contact.verified();
        contactRepository.save(contact);

        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByNickname(user.getNickname());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 닉네임으로 회원 존재 여부 조회시 거짓을 반환한다")
    void givenNonExisting_whenExistsByNickname_thenReturn() {
        // given
        String nickname = "테스터";

        // when
        boolean result = userRepository.existsByNickname(nickname);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 아이디 저장시 예외가 발생한다")
    void givenSameUsernames_whenSave_thenThrow() {
        // given
        Contact contact1 = createContact("tester1@gabojait.com");
        Contact contact2 = createContact("tester2@gabojait.com");
        contact1.verified();
        contact2.verified();
        contactRepository.saveAll(List.of(contact1, contact2));

        User user1 = createUser("tester", "테스터1", contact1);
        User user2 = createUser("tester", "테스터2", contact1);

        // when & then
        assertThatThrownBy(() -> userRepository.saveAll(List.of(user1, user2)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("동일한 닉네임 저장시 예외가 발생한다")
    void givenSameNicknames_whenSave_thenThrow() {
        // given
        Contact contact1 = createContact("tester1@gabojait.com");
        Contact contact2 = createContact("tester2@gabojait.com");
        contact1.verified();
        contact2.verified();
        contactRepository.saveAll(List.of(contact1, contact2));

        User user1 = createUser("tester1", "테스터", contact1);
        User user2 = createUser("tester2", "테스터", contact1);

        // when & then
        assertThatThrownBy(() -> userRepository.saveAll(List.of(user1, user2)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("연락처가 없는 회원 저장시 예외가 발생한다")
    void givenNoContact_whenSave_thenThrow() {
        // given
        User user = createUser("tester", "테스터", null);

        // when & then
        assertThatThrownBy(() -> userRepository.save(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("전체 포지션으로 회원 페이징 조회가 정상 작동한다")
    void givenNonePosition_whenFindPage_thenReturn() {
        // given
        Contact contact1 = createContact("tester1@gabojait.com");
        contactRepository.save(contact1);
        User user1 = createUser("tester1", "테스터일", contact1);
        userRepository.save(user1);

        Contact contact2 = createContact("tester2@gabojait.com");
        contactRepository.save(contact2);
        User user2 = createUser("tester2", "테스터이", contact2);
        userRepository.save(user2);

        Contact contact3 = createContact("tester3@gabojait.com");
        contactRepository.save(contact3);
        User user3 = createUser("tester3", "테스터삼", contact3);
        userRepository.save(user3);

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;
        Position position = Position.NONE;

        // when
        PageData<List<User>> users = userRepository.findPage(position, pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(users.getData()).containsExactly(user3, user2),
                () -> assertThat(users.getTotal()).isEqualTo(3L)
        );
    }

    @Test
    @DisplayName("특정 포지션으로 회원 페이징 조회가 정상 작동한다")
    void givenPosition_whenFindPage_thenReturn() {
        // given
        Contact contact1 = createContact("tester1@gabojait.com");
        contactRepository.save(contact1);
        User user1 = createUser("tester1", "테스터일", contact1);
        user1.updatePosition(Position.BACKEND);
        userRepository.save(user1);

        Contact contact2 = createContact("tester2@gabojait.com");
        contactRepository.save(contact2);
        User user2 = createUser("tester2", "테스터이", contact2);
        user2.updatePosition(Position.BACKEND);
        userRepository.save(user2);

        Contact contact3 = createContact("tester3@gabojait.com");
        contactRepository.save(contact3);
        User user3 = createUser("tester3", "테스터삼", contact3);
        userRepository.save(user3);

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;
        Position position = Position.BACKEND;

        // when
        PageData<List<User>> users = userRepository.findPage(position, pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(users.getData()).containsExactly(user2, user1),
                () -> assertThat(users.getTotal()).isEqualTo(2L)
        );
    }

    @Test
    @DisplayName("결과가 없는 회원 페이징 조회가 정상 작동한다")
    void givenNoResult_whenFindPage_thenReturn() {
        // given
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;
        Position position = Position.NONE;

        // when
        PageData<List<User>> users = userRepository.findPage(position, pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(users.getData()).isEmpty(),
                () -> assertThat(users.getTotal()).isEqualTo(0L)
        );
    }

    @Test
    @DisplayName("회원 식별자로 팀을 찾는 회원 단건 조회가 정상 작동한다")
    void givenValid_whenFindSeekingTeam_thenReturn() {
        // given
        Contact contact = createContact("tester@gaobjait.com");
        contactRepository.save(contact);
        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when
        User foundUser = userRepository.findSeekingTeam(user.getId()).get();

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    @DisplayName("이메일로 회원 단건 조회가 정상 작동한다")
    void givenValid_whenFind_thenReturn() {
        // given
        Contact contact = createContact("tester@gabojait.com");
        contactRepository.save(contact);
        User user = createUser("tester", "테스터", contact);
        userRepository.save(user);

        // when
        User foundUser = userRepository.find(contact.getEmail()).get();

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    private User createUser(String username, String nickname, Contact contact) {
        return User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(LocalDateTime.now())
                .contact(contact)
                .build();
    }

    private Contact createContact(String email) {
        return Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
    }
}