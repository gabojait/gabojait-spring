package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

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

    private static Stream<Arguments> providerEquals() {
        LocalDateTime now = LocalDateTime.now();

        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);
        Skill skill = createSkill("스프링", Level.MID, true, user);

        User user1 = createDefaultUser("tester@gabojait.com", "000000", "tester1", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);
        User user2 = createDefaultUser("tester@gabojait.com", "000000", "tester2", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);
        Skill userSkill1 = createSkill("스프링", Level.MID, true, user1);
        Skill userSkill2 = createSkill("스프링", Level.MID, true, user2);

        return Stream.of(
                Arguments.of(skill, skill, true),
                Arguments.of(skill, new Object(), false),
                Arguments.of(
                        createSkill("스프링", Level.MID, true, user),
                        createSkill("스프링", Level.MID, true, user),
                        true
                ),
                Arguments.of(userSkill1, userSkill2, false),
                Arguments.of(
                        createSkill("스프링1", Level.MID, true, user),
                        createSkill("스프링2", Level.MID, true, user),
                        false
                ),
                Arguments.of(
                        createSkill("스프링", Level.MID, true, user),
                        createSkill("스프링", Level.HIGH, true, user),
                        false
                ),
                Arguments.of(
                        createSkill("스프링", Level.MID, true, user),
                        createSkill("스프링", Level.MID, false, user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 기술 객체를 비교한다.")
    @MethodSource("providerEquals")
    @DisplayName("기술 객체를 비교한다.")
    void givenProvider_whenEquals_thenReturn(Skill skill, Object object, boolean result) {
        // when & then
        assertThat(skill.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        LocalDateTime now = LocalDateTime.now();

        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);

        return Stream.of(
                Arguments.of(
                        createSkill("스프링", Level.MID, true, user),
                        createSkill("스프링", Level.MID, true, user),
                        true
                ),
                Arguments.of(
                        createSkill("스프링1", Level.MID, true, user),
                        createSkill("스프링2", Level.MID, true, user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 기술 해시코드를 비교한다.")
    @MethodSource("providerHashCode")
    @DisplayName("기술 해시코드를 비교한다.")
    void givenProvider_whenHashCode_thenReturn(Skill skill1, Skill skill2, boolean result) {
        // when
        int hashCode1 = skill1.hashCode();
        int hashCode2 = skill2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Skill createSkill(String skillName,
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

    private static User createDefaultUser(String email,
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