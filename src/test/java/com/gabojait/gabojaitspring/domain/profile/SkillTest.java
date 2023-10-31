package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SkillTest {

    @Test
    @DisplayName("기술을 생성한다.")
    void builder() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester",
                "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        String skillName = "스프링";
        Level level = Level.MID;
        boolean isExperienced = true;

        // when
        Skill skill = createSkill(skillName, level, isExperienced, user);

        // then
        assertThat(skill)
                .extracting("skillName", "level", "isExperienced")
                .containsExactly(skillName, level, isExperienced);
    }

    @Test
    @DisplayName("학력을 업데이트한다.")
    void update() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill = createSkill("스프링", Level.MID, true, user);

        String skillName = "노드";
        Level level = Level.LOW;
        boolean isExperienced = false;

        // when
        skill.update(skillName, level, isExperienced);

        // then
        assertThat(skill)
                .extracting("skillName", "level", "isExperienced")
                .containsExactly(skillName, level, isExperienced);
    }

    @Test
    @DisplayName("같은 객체인 기술을 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill = createSkill("스프링", Level.MID, true, user);

        // when
        boolean result = skill.equals(skill);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("같은 정보인 기술을 비교하면 동일하다.")
    void givenEqualData_whenEquals_thenReturn() {
        // given
        String skillName = "스프링";
        Level level = Level.MID;
        boolean isExperienced = true;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill1 = createSkill(skillName, level, isExperienced, user);
        Skill skill2 = createSkill(skillName, level, isExperienced, user);

        // when
        boolean result = skill1.equals(skill2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("다른 객체인 기술을 비교하면 동일하지 않다.")
    void givenUnequalInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill = createSkill("스프링", Level.MID, true, user);
        Object object = new Object();

        // when
        boolean result = skill.equals(object);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 회원인 기술을 비교하면 동일하지 않다.")
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

        String skillName = "스프링";
        Level level = Level.MID;
        boolean isExperienced = true;
        Skill skill1 = createSkill(skillName, level, isExperienced, user1);
        Skill skill2 = createSkill(skillName, level, isExperienced, user2);

        // when
        boolean result = skill1.equals(skill2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 기술명인 기술을 비교하면 동일하지 않다.")
    void givenUnequalSkillName_whenEquals_thenReturn() {
        // given
        String skillName1 = "스프링1";
        String skillName2 = "스프링2";

        Level level = Level.MID;
        boolean isExperienced = true;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill1 = createSkill(skillName1, level, isExperienced, user);
        Skill skill2 = createSkill(skillName2, level, isExperienced, user);

        // when
        boolean result = skill1.equals(skill2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 레벨인 기술을 비교하면 동일하지 않다.")
    void givenUnequalLevel_whenEquals_thenReturn() {
        // given
        Level level1 = Level.MID;
        Level level2 = Level.LOW;

        String skillName = "스프링";
        boolean isExperienced = true;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill1 = createSkill(skillName, level1, isExperienced, user);
        Skill skill2 = createSkill(skillName, level2, isExperienced, user);

        // when
        boolean result = skill1.equals(skill2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 경험 여부인 기술을 비교하면 동일하지 않다.")
    void givenUnequalIsExperienced_whenEquals_thenReturn() {
        // given
        boolean isExperienced1 = true;
        boolean isExperienced2 = false;

        String skillName = "스프링";
        Level level = Level.MID;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill1 = createSkill(skillName, level, isExperienced1, user);
        Skill skill2 = createSkill(skillName, level, isExperienced2, user);

        // when
        boolean result = skill1.equals(skill2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 기술의 해시코드는 같다.")
    void givenEqual_whenHashCode_thenReturn() {
        // given
        String skillName = "스프링";
        Level level = Level.MID;
        boolean isExperienced = true;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill1 = createSkill(skillName, level, isExperienced, user);
        Skill skill2 = createSkill(skillName, level, isExperienced, user);

        // when
        int hashCode1 = skill1.hashCode();
        int hashCode2 = skill2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("동일하지 않은 기술의 해시코드는 다르다.")
    void givenUnequal_whenHashCode_thenReturn() {
        // given
        String skillName1 = "스프링1";
        String skillName2 = "스프링2";

        Level level = Level.MID;
        boolean isExperienced = true;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Skill skill1 = createSkill(skillName1, level, isExperienced, user);
        Skill skill2 = createSkill(skillName2, level, isExperienced, user);

        // when
        int hashCode1 = skill1.hashCode();
        int hashCode2 = skill2.hashCode();

        // then
        assertThat(hashCode1).isNotEqualTo(hashCode2);
    }

    private Skill createSkill(String skillName,
                              Level level,
                              boolean isExperienced,
                              User user) {
        return Skill.builder()
                .skillName(skillName)
                .level(level)
                .isExperienced(isExperienced)
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