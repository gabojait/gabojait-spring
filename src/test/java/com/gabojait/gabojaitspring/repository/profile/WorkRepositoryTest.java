package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Work;
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
class WorkRepositoryTest {

    @Autowired private WorkRepository workRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("경력 전체 조회가 정상 작동한다")
    void givenValid_whenFindAll_thenReturn() {
        // given
        User user = createSavedDefaultUser();

        Work work1 = createWork("가보자잇사1", "백엔드개발1",
                LocalDate.of(2001, 1, 1),
                LocalDate.of(2002, 1, 1), false, user);
        Work work2 = createWork("가보자잇사2", "백엔드개발2",
                LocalDate.of(2002, 1, 1),
                LocalDate.of(2003, 1, 1), true, user);
        Work work3 = createWork("가보자잇사3", "백엔드개발3",
                LocalDate.of(2003, 1, 1),
                LocalDate.of(2004, 1, 1), false, user);
        workRepository.saveAll(List.of(work1, work2, work3));

        // when
        List<Work> works = workRepository.findAll(user.getId());

        // then
        assertThat(works).containsExactly(work3, work2, work1);
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