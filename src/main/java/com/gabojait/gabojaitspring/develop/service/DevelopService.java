package com.gabojait.gabojaitspring.develop.service;

import com.gabojait.gabojaitspring.common.util.NotificationProvider;
import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.repository.EducationRepository;
import com.gabojait.gabojaitspring.profile.repository.PortfolioRepository;
import com.gabojait.gabojaitspring.profile.repository.SkillRepository;
import com.gabojait.gabojaitspring.profile.repository.WorkRepository;
import com.gabojait.gabojaitspring.profile.service.EducationService;
import com.gabojait.gabojaitspring.profile.service.PortfolioService;
import com.gabojait.gabojaitspring.profile.service.SkillService;
import com.gabojait.gabojaitspring.profile.service.WorkService;
import com.gabojait.gabojaitspring.review.repository.ReviewRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
import com.gabojait.gabojaitspring.team.service.TeamService;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.ContactRepository;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.service.ContactService;
import com.gabojait.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class DevelopService {

    @Value("${api.name}")
    private String serverName;

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillRepository skillRepository;
    private final WorkRepository workRepository;
    private final TeamRepository teamRepository;
    private final OfferRepository offerRepository;
    private final ReviewRepository reviewRepository;
    private final ContactService contactService;
    private final UserService userService;
    private final EducationService educationService;
    private final PortfolioService portfolioService;
    private final SkillService skillService;
    private final WorkService workService;
    private final TeamService teamService;
    private final UtilityProvider utilityProvider;
    private final NotificationProvider notificationProvider;

    /**
     * 서버명 변환 | main
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * 테스트 알림 전송 | main |
     * 500(SERVER_ERROR)
     */
    public void testNotification(User user, String title, String message) {
        notificationProvider.singleNotification(user, title, message);
    }
    /**
     * 데이터베이스 초기화 | main&sub |
     * 500(SERVER_ERROR)
     */
    public void injectTestData() {
        resetDatabase();

        List<Contact> contacts = injectContacts(10);
        List<User> users = injectUsers(contacts);

        injectEducationsAndWorks(users.subList(0, 3));
        injectPortfolios(users.subList(0, 3));
        injectPositionAndSkills(users.subList(0, 3));
        injectProfileDescriptions(users.subList(0, 3));

        injectTeam(users.get(0));
    }

    private List<Contact> injectContacts(int n) {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            Contact contact = Contact.builder()
                    .email("test" + i + "@gabojait.com")
                    .verificationCode("000000")
                    .build();
            contact.verified();
            contact.registered();
            contactService.save(contact);

            contacts.add(contact);
        }

        return contacts;
    }

    private List<User> injectUsers(List<Contact> contacts) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            int n = i + 1;

            User user = User.userBuilder()
                    .username("test" + n)
                    .legalName("테스트")
                    .password(utilityProvider.encodePassword("password1!"))
                    .gender(Gender.MALE)
                    .birthdate(LocalDate.of(2000, 1, n))
                    .contact(contacts.get(i))
                    .nickname("테스트" + n)
                    .fcmToken("")
                    .build();
            userService.save(user);

            users.add(user);
        }

        return users;
    }

    private void injectProfileDescriptions(List<User> users) {
        for (User user: users)
            userService.updateProfileDescription(user, "안녕하세요.");
    }

    private void injectEducationsAndWorks(List<User> users) {
        for (User user : users) {
            Education education = Education.builder()
                    .userId(user.getId())
                    .institutionName("가보자잇대")
                    .startedDate(LocalDate.of(2020, 3, 1))
                    .endedDate(LocalDate.of(2021, 12, 20))
                    .isCurrent(false)
                    .build();
            educationService.save(education);

            Work work = Work.builder()
                    .userId(user.getId())
                    .corporationName("가보자잇사")
                    .startedDate(LocalDate.of(2020, 3, 1))
                    .endedDate(LocalDate.of(2021, 12, 20))
                    .isCurrent(false)
                    .workDescription("가보자잇사에서 백엔드 개발")
                    .build();
            workService.save(work);

            userService.updateEducationsAndWorks(user,
                    List.of(education),
                    List.of(),
                    List.of(),
                    List.of(work),
                    List.of(),
                    List.of());
        }
    }

    private void injectPortfolios(List<User> users) {
        for (User user : users) {
            Portfolio portfolio = Portfolio.builder()
                    .userId(user.getId())
                    .portfolioName("깃허브")
                    .url("github.com/gabojait")
                    .media(Media.LINK)
                    .build();
            portfolioService.save(portfolio);

            userService.updatePortfolios(user, List.of(portfolio), List.of(), List.of());
        }
    }

    private void injectPositionAndSkills(List<User> users) {
        for (User user : users) {
            Skill skill = Skill.builder()
                    .userId(user.getId())
                    .skillName("스프링")
                    .level(Level.MID)
                    .isExperienced(true)
                    .build();
            skillService.save(skill);

            userService.updatePositionAndSkills(user, Position.BACKEND.name(), List.of(skill), List.of(), List.of());
        }
    }

    private void injectTeam(User user) {
        Team team = Team.builder()
                .leaderUserId(user.getId())
                .projectName("가보자잇")
                .projectDescription("가보자잇 프로젝트 설명입니다.")
                .designerTotalRecruitCnt((short) 2)
                .backendTotalRecruitCnt((short) 2)
                .frontendTotalRecruitCnt((short) 2)
                .managerTotalRecruitCnt((short) 2)
                .openChatUrl("https://open.kakao.com/o/test")
                .expectation("열정적인 팀원을 구합니다.")
                .build();

        teamService.create(team, user);
        userService.joinTeam(user, team, true);
    }

    private void resetDatabase() {
        try {
            contactRepository.deleteAll();
            userRepository.deleteAll();
            educationRepository.deleteAll();
            portfolioRepository.deleteAll();
            skillRepository.deleteAll();
            workRepository.deleteAll();
            teamRepository.deleteAll();
            reviewRepository.deleteAll();
            offerRepository.deleteAll();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
