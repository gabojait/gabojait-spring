package com.gabojait.gabojaitspring.domain.report;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockTest {

    @Test
    @DisplayName("차단 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        User user = createDefaultUser("tester1", "테스터일", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User blocker = createDefaultUser("tester2", "테스터이", LocalDate.of(1997, 2, 11), LocalDateTime.now());

        LocalDate startAt = LocalDate.of(2000, 1, 1);
        LocalDate endAt = LocalDate.now().plusDays(1L);

        // when
        Block block = createBlock(startAt, endAt, user, blocker);

        // then
        assertThat(block)
                .extracting("startAt", "endAt", "user", "blocker")
                .containsExactly(startAt, endAt, user, blocker);
    }

    private static Block createBlock(LocalDate startAt, LocalDate endAt, User user, User blocker) {
        return Block.builder()
                .startAt(startAt)
                .endAt(endAt)
                .user(user)
                .blocker(blocker)
                .build();
    }

    private static User createDefaultUser(String username,
                                          String nickname,
                                          LocalDate birthdate,
                                          LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}
