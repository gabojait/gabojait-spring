package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WorkTest {

    @Test
    @DisplayName("경력을 생성한다.")
    void builder() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String corporationName = "가보자잇사";
        String workDescription = "가보자잇사에서 근무";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        boolean isCurrent = false;

        // when
        Work work = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent, user);

        // then
        assertThat(work)
                .extracting("corporationName", "workDescription", "startedAt", "endedAt", "isCurrent")
                .containsExactly(corporationName, workDescription, startedAt, endedAt, isCurrent);
    }

    @Test
    @DisplayName("경력을 업데이트한다.")
    void update() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work = createWork("가보자잇사", "백엔드 개발", LocalDate.of(2001, 1, 1), LocalDate.of(2002, 1, 1), false, user);

        String corporationName = "카카오";
        String workDescription = "프런트 개발";
        LocalDate startedAt = LocalDate.of(2003, 1, 1);
        LocalDate endedAt = LocalDate.of(2004, 1, 1);
        boolean isCurrent = false;

        // when
        work.update(corporationName, workDescription, startedAt, endedAt, isCurrent);

        // then
        assertThat(work)
                .extracting("corporationName", "workDescription", "startedAt", "endedAt", "isCurrent")
                .containsExactly(corporationName, workDescription, startedAt, endedAt, isCurrent);
    }

    private Work createWork(String corporationName,
                            String workDescription,
                            LocalDate startedAt,
                            LocalDate endedAt,
                            boolean isCurrent,
                            User user) {
        return Work.builder()
                .corporationName(corporationName)
                .workDescription(workDescription)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .isCurrent(isCurrent)
                .user(user)
                .build();
    }

    private User createDefaultUser(String email,
                                   String verificationCode,
                                   String username,
                                   String password,
                                   String nickname,
                                   Gender gender,
                                   LocalDate birthdate,
                                   LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}