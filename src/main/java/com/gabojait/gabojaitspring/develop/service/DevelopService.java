package com.gabojait.gabojaitspring.develop.service;

import com.gabojait.gabojaitspring.common.util.FcmProvider;
import com.gabojait.gabojaitspring.common.util.PasswordProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
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
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.review.repository.ReviewRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.repository.TeamMemberRepository;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.UserRole;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.repository.ContactRepository;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DevelopService {

    @Value("${api.name}")
    private String serverName;

    @PersistenceContext
    private final EntityManager entityManager;

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillRepository skillRepository;
    private final WorkRepository workRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PasswordProvider passwordProvider;
    private final FcmProvider fcmProvider;
    private final ReviewRepository reviewRepository;

    /**
     * 서버명 반환
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * 테스터 단건 조회 |
     * 404(TESTER_NOT_FOUND)
     */
    public User findOneTester(String username) {
        return userRepository.findByUsernameAndIsDeletedIsFalse(username)
                .orElseThrow(() -> {
                    throw new CustomException(TESTER_NOT_FOUND);
                });
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneUser(Long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 데이터베이스 초기화 후 테스트 데이터 주입
     */
    public void resetAndInjectData() {
        resetDatabase();

        List<Contact> contacts = injectContacts();
        List<User> users = injectUsers(contacts);
        injectUserRoles(users);

        injectProfileDescriptions(users);
        injectEducationsAndWorks(users);
        injectPortfolios(users);
        injectPositionAndSkills(users);

        injectCompleteTeamAndReview(users);
        injectCurrentTeam(users);
    }

    /**
     * 테스트 FCM 전송 |
     * 404(USER_NOT_FOUND)
     */
    public void sendTestFcm(long userId, String title, String body) {
        User user = findOneUser(userId);

        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        data.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        fcmProvider.sendOne(user, data);
    }

    /**
     * 연락처 주입
     */
    private List<Contact> injectContacts() {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Contact contact = Contact.builder()
                    .email("test" + i + "@gabojait.com")
                    .verificationCode("000000")
                    .build();

            contact.verified();

            contactRepository.save(contact);

            contacts.add(contact);
        }

        return contacts;
    }

    /**
     * 회원 주입
     */
    private List<User> injectUsers(List<Contact> contacts) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            int n = i + 1;

            User user = User.builder()
                    .username("test" + n)
                    .password(passwordProvider.encodePassword("password1!"))
                    .gender(Gender.N)
                    .birthdate(LocalDate.of(2000, 1, (i % 30) + 1))
                    .nickname("테스트" + n)
                    .contact(contacts.get(i))
                    .build();

            userRepository.save(user);

            users.add(user);
        }

        return users;
    }

    /**
     * 회원 권한 주입
     */
    private void injectUserRoles(List<User> users) {
        for (User user : users) {
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(Role.USER)
                    .build();

            userRoleRepository.save(userRole);
        }
    }

    /**
     * 프로필 설명 주입
     */
    private void injectProfileDescriptions(List<User> users) {
        for (User user: users)
            user.updateProfileDescription("안녕하세요.");
    }

    /**
     * 학력과 경력 주입
     */
    private void injectEducationsAndWorks(List<User> users) {
        for (User user : users) {
            Education education = Education.builder()
                    .institutionName("가보자잇대")
                    .startedAt(LocalDate.of(2020, 3, 1))
                    .endedAt(LocalDate.of(2021, 12, 20))
                    .isCurrent(false)
                    .user(user)
                    .build();

            educationRepository.save(education);

            Work work = Work.builder()
                    .corporationName("가보자잇사")
                    .workDescription("가보자잇사에서 백엔드 개발")
                    .startedAt(LocalDate.of(2020, 3, 1))
                    .endedAt(LocalDate.of(2021, 12, 20))
                    .isCurrent(false)
                    .user(user)
                    .build();

            workRepository.save(work);
        }
    }

    /**
     * 포트폴리오들 주입
     */
    private void injectPortfolios(List<User> users) {
        for (User user : users) {
            Portfolio portfolio = Portfolio.builder()
                    .portfolioName("깃허브")
                    .portfolioUrl("github.com/gabojait")
                    .media(Media.LINK)
                    .user(user)
                    .build();

            portfolioRepository.save(portfolio);
        }
    }

    /**
     * 포지션과 기술들 주입
     */
    private void injectPositionAndSkills(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            Position position;

            if (i < 50)
                switch (i % 4) {
                    case 0:
                        position = Position.DESIGNER;
                        break;
                    case 1:
                        position = Position.BACKEND;
                        break;
                    case 2:
                        position = Position.FRONTEND;
                        break;
                    case 3:
                        position = Position.MANAGER;
                        break;
                    default:
                        position = Position.NONE;
                        break;
                }
            else
                position = Position.NONE;

            users.get(i).updatePosition(position);

            Skill skill = Skill.builder()
                    .skillName("스프링")
                    .level(Level.MID)
                    .isExperienced(true)
                    .user(users.get(i))
                    .build();

            skillRepository.save(skill);
        }
    }

    /**
     * 완료 팀 및 리뷰 주입
     */
    private void injectCompleteTeamAndReview(List<User> users) {
        for (int i = 0; i < 25; i += 5) {
            Team team = Team.builder()
                    .projectName("가볼까잇팀" + (i + 1))
                    .projectDescription("가보자잇 프로젝트 설명입니다.")
                    .designerCnt((byte) 2)
                    .backendCnt((byte) 2)
                    .frontendCnt((byte) 2)
                    .managerCnt((byte) 2)
                    .isDesignerFull(false)
                    .isBackendFull(false)
                    .isFrontendFull(false)
                    .isManagerFull(false)
                    .expectation("열정적인 팀원을 구합니다.")
                    .openChatUrl("https://open.kakao.com/o/test")
                    .build();

            teamRepository.save(team);

            for (int j = i; j < i + 5; j++) {
                boolean isLeader = j % 5 == 0;

                TeamMember teamMember = TeamMember.builder()
                        .user(users.get(j))
                        .team(team)
                        .position(users.get(i).getPosition())
                        .isLeader(isLeader)
                        .build();

                teamMember.delete();

                teamMemberRepository.save(teamMember);

                if (j % 5 == 4)
                    team.complete("github.com/gabojait.com");
            }

            for (int j = i; j < i + 5; j++) {
                User reviewee = users.get(j);
                for (int k = i; k < i + 5; k++) {
                    if (j == k)
                        continue;

                    User reviewer = users.get(k);
                    Random random = new Random();
                    byte rate = (byte) (random.nextInt(5) + 1);

                    Review review = Review.builder()
                            .reviewer(reviewer)
                            .reviewee(reviewee)
                            .team(team)
                            .rate(rate)
                            .post("열정적인 팀원이였습니다.")
                            .build();

                    reviewRepository.save(review);

                    reviewee.rate(rate);
                }
            }
        }
    }

    /**
     * 현재 팀 주입
     */
    private void injectCurrentTeam(List<User> users) {
        for (int i = 0; i < 50; i++) {
            Team team = Team.builder()
                    .projectName("가보자잇팀" + (i + 1))
                    .projectDescription("가보자잇 프로젝트 설명입니다.")
                    .designerCnt((byte) 2)
                    .backendCnt((byte) 2)
                    .frontendCnt((byte) 2)
                    .managerCnt((byte) 2)
                    .isDesignerFull(false)
                    .isBackendFull(false)
                    .isFrontendFull(false)
                    .isManagerFull(false)
                    .expectation("열정적인 팀원을 구합니다.")
                    .openChatUrl("https://open.kakao.com/o/test")
                    .build();

            teamRepository.save(team);

            TeamMember teamMember = TeamMember.builder()
                    .user(users.get(i))
                    .team(team)
                    .position(users.get(i).getPosition())
                    .isLeader(true)
                    .build();

            users.get(i).updateIsSeekingTeam(false);
            userRepository.save(users.get(i));

            teamMemberRepository.save(teamMember);
        }
    }

    /**
     * 데이터 베이스 초기화 |
     * 500(SERVER_ERROR)
     */
    private void resetDatabase() {
        try {
            entityManager.createNativeQuery("DELETE FROM education").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM portfolio").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM skill").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM work").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM fcm").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM team_member").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM favorite_user").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM favorite_team").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM offer").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM review").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM team").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM user_role").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM admin").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM contact").executeUpdate();

            entityManager.createNativeQuery("ALTER TABLE education AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE portfolio AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE skill AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE work AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE fcm AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE team_member AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE favorite_user AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE favorite_team AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE offer AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE review AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE team AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE user_role AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE admin AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE contact AUTO_INCREMENT = 1").executeUpdate();

        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
