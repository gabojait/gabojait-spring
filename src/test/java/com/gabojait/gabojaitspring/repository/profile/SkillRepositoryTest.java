package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Level;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SkillRepositoryTest {

    @Autowired private SkillRepository skillRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("기술 전체 조회가 정상 작동한다")
    void givenValid_whenFindAll_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Skill skill1 = createSkill("스프링1", Level.HIGH, true, user);
        Skill skill2 = createSkill("스프링2", Level.MID, false, user);
        Skill skill3 = createSkill("스프링3", Level.LOW, true, user);
        skillRepository.saveAll(List.of(skill1, skill2, skill3));

        // when
        List<Skill> skills = skillRepository.findAll(user.getId());

        // then
        assertThat(skills).containsExactly(skill3, skill2, skill1);
    }

    @Test
    @DisplayName("여러 회원의 기술 전체 조회가 정상 작동한다")
    void givenValid_whenFindAllInFetchUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Skill skill1 = createSkill("스프링1", Level.HIGH, true, user1);
        Skill skill2 = createSkill("노드1", Level.MID, false, user1);
        Skill skill3 = createSkill("스프링2", Level.LOW, true, user2);
        Skill skill4 = createSkill("노드2", Level.MID, false, user2);
        Skill skill5 = createSkill("스프링3", Level.LOW, true, user3);
        Skill skill6 = createSkill("노드3", Level.MID, false, user3);

        skillRepository.saveAll(List.of(skill1, skill2, skill3, skill4, skill5, skill6));

        // when
        List<Skill> skills = skillRepository.findAllInFetchUser(List.of(user1.getId(), user2.getId(), user3.getId()));

        // then
        assertThat(skills).containsExactly(skill6, skill5, skill4, skill3, skill2, skill1);
    }

    private Skill createSkill(String skillName, Level level, boolean isExperienced, User user) {
        return Skill.builder()
                .skillName(skillName)
                .level(level)
                .isExperienced(isExperienced)
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
        return userRepository.save(user);
    }
}