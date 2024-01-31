package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Education;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class EducationRepositoryTest {

    @Autowired private EducationRepository educationRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("학력 전체 조회가 정상 작동한다")
    void givenValid_whenFindAll_thenReturn() {
        // given
        User user = createSavedDefaultUser();

        Education education1 = createEducation("가보자잇사1",
                LocalDate.of(2001, 1, 1),
                LocalDate.of(2002, 1, 1), false, user);
        Education education2 = createEducation("가보자잇사2",
                LocalDate.of(2002, 1, 1),
                LocalDate.of(2003, 1, 1), false, user);
        Education education3 = createEducation("가보자잇사3",
                LocalDate.of(2003, 1, 1),
                LocalDate.of(2004, 1, 1), false, user);
        educationRepository.saveAll(List.of(education1, education2, education3));

        // when
        List<Education> educations = educationRepository.findAll(user.getId());

        // then
        assertThat(educations).containsExactly(education3, education2, education1);
    }

    private Education createEducation(String institutionName,
                                      LocalDate startedAt,
                                      LocalDate endedAt,
                                      Boolean isCurrent,
                                      User user) {
        return Education.builder()
                .user(user)
                .institutionName(institutionName)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .isCurrent(isCurrent)
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
        return userRepository.save(user);
    }
}