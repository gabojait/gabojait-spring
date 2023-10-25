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