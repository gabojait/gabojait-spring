package com.gabojait.gabojaitspring.api.service.develop;

import com.gabojait.gabojaitspring.api.dto.user.response.UserDefaultResponse;
import com.gabojait.gabojaitspring.domain.profile.*;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.domain.user.*;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.profile.EducationRepository;
import com.gabojait.gabojaitspring.repository.profile.PortfolioRepository;
import com.gabojait.gabojaitspring.repository.profile.SkillRepository;
import com.gabojait.gabojaitspring.repository.profile.WorkRepository;
import com.gabojait.gabojaitspring.repository.review.ReviewRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import com.gabojait.gabojaitspring.repository.user.UserRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.TESTER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DevelopServiceTest {

    @Autowired private DevelopService developService;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private EducationRepository educationRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private WorkRepository workRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;

    @Test
    @DisplayName("서버명을 조회한다.")
    void givenValid_whenGetServerName_thenReturn() {
        // given & when
        String serverName = developService.getServerName();

        // then
        assertEquals("Gabojait Test", serverName);
    }

    @Test
    @DisplayName("테스트 회원을 조회한다.")
    void givenValid_whenFindTester_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1");

        // when
        UserDefaultResponse response = developService.findTester(user.getId());

        // then
        assertAll(
                () -> assertThat(response)
                        .extracting("userId", "username", "nickname", "gender", "birthdate", "isNotified", "createdAt",
                                "updatedAt")
                        .containsExactly(user.getId(), user.getUsername(), user.getNickname(), user.getGender(),
                                user.getBirthdate(), user.getIsNotified(), user.getCreatedAt(),
                                user.getUpdatedAt()),
                () -> assertThat(response.getContact())
                        .extracting("contactId", "email", "createdAt", "updatedAt")
                        .containsExactly(user.getContact().getId(), user.getContact().getEmail(),
                                user.getContact().getCreatedAt(), user.getContact().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("존재하지 않은 테스트 회원 식별자로 테스트 회원을 조회하면 예외가 발생한다.")
    void givenNonExistingUser_whenFindTester_thenThrow() {
        // given
        long testerId = Long.MAX_VALUE;

        // when & then
        assertThatThrownBy(() -> developService.findTester(testerId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TESTER_NOT_FOUND);
    }

    @Test
    @DisplayName("테스트 연락처 데이터를 주입한다.")
    void givenValid_whenInjectContacts_thenReturn() {
        // given & when
        List<Contact> contacts = developService.injectContacts();

        // then
        assertAll(
                () -> assertEquals(100, contacts.size()),
                () -> assertThat(contacts.get(0))
                        .extracting("email", "verificationCode", "isVerified")
                        .containsExactly("tester1@gabojait.com", "000000", true)
        );
    }

    @Test
    @DisplayName("테스트 회원 데이터를 주입한다.")
    void givenValid_whenInjectUsers_thenReturn() {
        // given
        List<Contact> contacts = developService.injectContacts();

        // when
        List<User> users = developService.injectUsers(contacts);

        // then
        assertAll(
                () -> assertEquals(100, users.size()),
                () -> assertThat(users.get(0))
                        .extracting("username", "nickname", "profileDescription", "imageUrl", "birthdate", "gender",
                                "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam", "isTemporaryPassword",
                                "isNotified")
                        .containsExactly("tester1", "테스터1", null, null, LocalDate.of(2000, 1, 1), Gender.N,
                                Position.NONE, 0F, 0L, 0, true, false, true)
        );
    }

    @Test
    @DisplayName("테스트 회원 권한 데이터를 주입한다.")
    void givenValid_whenInjectUserRoles_thenReturn() {
        // given
        List<Contact> contacts = developService.injectContacts();
        List<User> users = developService.injectUsers(contacts);

        // when
        developService.injectUserRoles(users);

        // then
        List<UserRole> userRoles = userRoleRepository.findAll(users.get(0).getUsername());
        assertThat(userRoles)
                .extracting("role")
                .containsExactly(Role.USER);
    }

    @Test
    @DisplayName("테스트 프로필 데이터를 주입한다.")
    void givenValid_whenInjectProfiles_thenReturn() {
        // given
        List<Contact> contacts = developService.injectContacts();
        List<User> users = developService.injectUsers(contacts);

        // when
        developService.injectProfiles(users);

        // then
        User user = users.get(0);
        List<Education> educations = educationRepository.findAll(user.getId());
        List<Portfolio> portfolios = portfolioRepository.findAll(user.getId());
        List<Skill> skills = skillRepository.findAll(user.getId());
        List<Work> works = workRepository.findAll(user.getId());

        assertAll(
                () -> assertThat(user)
                        .extracting("position", "profileDescription")
                        .containsExactly(Position.DESIGNER, "안녕하세요."),
                () -> assertThat(educations)
                        .extracting("institutionName", "startedAt", "endedAt", "isCurrent")
                        .containsExactly(
                                tuple("가보자잇대", LocalDate.of(2020, 3, 1), LocalDate.of(2021, 12, 20), false)
                        ),
                () -> assertThat(portfolios)
                        .extracting("portfolioName", "portfolioUrl", "media")
                        .containsExactly(
                                tuple("깃허브", "github.com/gabojait", Media.LINK)
                        ),
                () -> assertThat(skills)
                        .extracting("skillName", "level", "isExperienced")
                        .containsExactly(
                                tuple("스프링", Level.MID, true)
                        ),
                () -> assertThat(works)
                        .extracting("corporationName", "workDescription", "startedAt", "endedAt", "isCurrent")
                        .containsExactly(
                                tuple("가보자잇사", "가보자잇사에서 백엔드 개발", LocalDate.of(2020, 3, 1),
                                        LocalDate.of(2021, 12, 20), false)
                        )
        );
    }

    @Test
    @DisplayName("테스트 완료한 팀 데이터를 주입한다.")
    void givenValid_whenInjectCompleteTeams_thenReturn() {
        // given
        List<Contact> contacts = developService.injectContacts();
        List<User> users = developService.injectUsers(contacts);
        developService.injectProfiles(users);

        // when
        developService.injectCompleteTeams(users);

        // then
        User user = users.get(0);
        List<TeamMember> teamMembers = teamMemberRepository.findAllFetchTeam(user.getId());
        Page<Review> reviews = reviewRepository.findPage(user.getId(), Long.MAX_VALUE, 20);

        assertAll(
                () -> assertThat(teamMembers)
                        .extracting("position", "teamMemberStatus", "isLeader", "isDeleted")
                        .containsExactly(
                                tuple(user.getPosition(), TeamMemberStatus.COMPLETE, true, false)
                        ),
                () -> assertThat(teamMembers.get(0).getTeam())
                        .extracting("projectName", "projectDescription", "expectation", "openChatUrl", "designerMaxCnt",
                                "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt", "projectUrl", "visitedCnt", "isRecruiting",
                                "isDeleted")
                        .containsExactly("가볼까잇1", "가볼까잇 프로젝트 설명입니다.", "열정적인 팀원을 구합니다.",
                                "https://open.kakao.com/o/test", (byte) 5, (byte) 5, (byte) 5, (byte) 5, "github.com/gabojait",
                                0L, false, false),
                () -> assertThat(reviews.getContent())
                        .extracting("post")
                        .containsExactly("열정적인 팀원이였습니다.", "열정적인 팀원이였습니다.", "열정적인 팀원이였습니다.",
                                "열정적인 팀원이였습니다.")
        );
    }

    @Test
    @DisplayName("테스트 현재 팀 데이터를 주입한다.")
    void givenValid_whenInjectCurrentTeams_thenReturn() {
        // given
        List<Contact> contacts = developService.injectContacts();
        List<User> users = developService.injectUsers(contacts);
        developService.injectProfiles(users);

        // when
        developService.injectCurrentTeams(users);

        // then
        List<TeamMember> teamMembers = teamMemberRepository.findAllFetchTeam(users.get(0).getId());

        assertAll(
                () -> assertThat(teamMembers)
                        .extracting("position", "teamMemberStatus", "isLeader", "isDeleted")
                        .containsExactly(
                                tuple(users.get(0).getPosition(), TeamMemberStatus.PROGRESS, true, false)
                        ),
                () -> assertThat(teamMembers.get(0).getTeam())
                        .extracting("projectName", "projectDescription", "expectation", "openChatUrl", "designerMaxCnt",
                                "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt", "projectUrl", "visitedCnt",
                                "isRecruiting", "isDeleted")
                        .containsExactly("가보자잇1", "가보자잇 프로젝트 설명입니다.", "열정적인 팀원을 구합니다.",
                                "https://open.kakao.com/o/test", (byte) 2, (byte) 2, (byte) 2, (byte) 2, null, 0L,
                                true, false)
        );
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