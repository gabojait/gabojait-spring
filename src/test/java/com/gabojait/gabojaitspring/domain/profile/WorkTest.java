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

    @Test
    @DisplayName("같은 객체인 경력을 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work = createWork("가보자잇사", "백엔드 개발", LocalDate.of(2001, 1, 1), LocalDate.of(2002, 1, 1), false, user);

        // when
        boolean result = work.equals(work);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("같은 정보인 경력을 비교하면 동일하다.")
    void givenEqualData_whenEquals_thenReturn() {
        // given
        String corporationName = "가보자잇사";
        String workDescription = "백엔드 개발";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work1 = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent, user);
        Work work2 = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent, user);

        // when
        boolean result = work1.equals(work2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("다른 객체인 경력을 비교하면 동일하지 않다.")
    void givenUnequalInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work = createWork("가보자잇사", "백엔드 개발", LocalDate.of(2001, 1, 1), LocalDate.of(2002, 1, 1), false, user);
        Object object = new Object();

        // when
        boolean result = work.equals(object);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 회원인 경력을 비교하면 동일하지 않다.")
    void givenUnequalUser_whenEquals_thenReturn() {
        // given
        String username1 = "tester1";
        String username2 = "tester2";
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username1, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username2, password, nickname, gender, birthdate, now);

        String corporationName = "가보자잇사";
        String workDescription = "백엔드 개발";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        boolean isCurrent = false;
        Work work1 = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent, user1);
        Work work2 = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent, user2);

        // when
        boolean result = work1.equals(work2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 기관명인 경력을 비교하면 동일하지 않다.")
    void givenUnequalCorporationName_whenEquals_thenReturn() {
        // given
        String corporationName1 = "가보자잇사1";
        String corporationName2 = "가보자잇사2";

        String workDescription = "백엔드 개발";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work1 = createWork(corporationName1, workDescription, startedAt, endedAt, isCurrent, user);
        Work work2 = createWork(corporationName2, workDescription, startedAt, endedAt, isCurrent, user);

        // when
        boolean result = work1.equals(work2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 경력 설명인 경력을 비교하면 동일하지 않다.")
    void givenUnequalWorkDescription_whenEquals_thenReturn() {
        // given
        String workDescription1 = "백엔드 개발1";
        String workDescription2 = "백엔드 개발2";

        String corporationName = "가보자잇사";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work1 = createWork(corporationName, workDescription1, startedAt, endedAt, isCurrent, user);
        Work work2 = createWork(corporationName, workDescription2, startedAt, endedAt, isCurrent, user);

        // when
        boolean result = work1.equals(work2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 시작일인 경력을 비교하면 동일하지 않다.")
    void givenUnequalStartedAt_whenEquals_thenReturn() {
        // given
        LocalDate startedAt1 = LocalDate.of(2001, 1, 1);
        LocalDate startedAt2 = LocalDate.of(2001, 1, 2);

        String corporationName = "가보자잇사";
        String workDescription = "백엔드 개발";
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work1 = createWork(corporationName, workDescription, startedAt1, endedAt, isCurrent, user);
        Work work2 = createWork(corporationName, workDescription, startedAt2, endedAt, isCurrent, user);

        // when
        boolean result = work1.equals(work2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 종료일인 경력을 비교하면 동일하지 않다.")
    void givenUnequalEndedAt_whenEquals_thenReturn() {
        // given
        LocalDate endedAt1 = LocalDate.of(2002, 1, 1);
        LocalDate endedAt2 = LocalDate.of(2002, 1, 2);

        String corporationName = "가보자잇사";
        String workDescription = "백엔드 개발";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work1 = createWork(corporationName, workDescription, startedAt, endedAt1, isCurrent, user);
        Work work2 = createWork(corporationName, workDescription, startedAt, endedAt2, isCurrent, user);

        // when
        boolean result = work1.equals(work2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 현재 여부인 경력을 비교하면 동일하지 않다.")
    void givenUnequalIsCurrent_whenEquals_thenReturn() {
        // given
        boolean isCurrent1 = false;
        boolean isCurrent2 = true;

        String corporationName = "가보자잇사";
        String workDescription = "백엔드 개발";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work1 = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent1, user);
        Work work2 = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent2, user);

        // when
        boolean result = work1.equals(work2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 경력의 해시코드는 같다.")
    void givenEqual_whenHashCode_thenReturn() {
        // given
        String corporationName = "가보자잇사";
        String workDescription = "백엔드 개발";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work1 = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent, user);
        Work work2 = createWork(corporationName, workDescription, startedAt, endedAt, isCurrent, user);

        // when
        int hashCode1 = work1.hashCode();
        int hashCode2 = work2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("동일하지 않은 경력의 해시코드는 다르다.")
    void givenUnequal_whenHashCode_thenReturn() {
        // given
        String corporationName1 = "가보자잇사1";
        String corporationName2 = "가보자잇사2";

        String workDescription = "백엔드 개발";
        LocalDate startedAt = LocalDate.of(2001, 1, 1);
        LocalDate endedAt = LocalDate.of(2002, 1, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Work work1 = createWork(corporationName1, workDescription, startedAt, endedAt, isCurrent, user);
        Work work2 = createWork(corporationName2, workDescription, startedAt, endedAt, isCurrent, user);

        // when
        int hashCode1 = work1.hashCode();
        int hashCode2 = work2.hashCode();

        // then
        assertThat(hashCode1).isNotEqualTo(hashCode2);
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