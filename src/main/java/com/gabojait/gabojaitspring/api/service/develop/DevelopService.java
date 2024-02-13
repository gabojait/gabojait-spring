package com.gabojait.gabojaitspring.api.service.develop;

import com.gabojait.gabojaitspring.common.util.PasswordUtility;
import com.gabojait.gabojaitspring.domain.profile.*;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.*;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.repository.favorite.FavoriteRepository;
import com.gabojait.gabojaitspring.repository.notification.FcmRepository;
import com.gabojait.gabojaitspring.repository.notification.NotificationRepository;
import com.gabojait.gabojaitspring.repository.offer.OfferRepository;
import com.gabojait.gabojaitspring.repository.profile.EducationRepository;
import com.gabojait.gabojaitspring.repository.profile.PortfolioRepository;
import com.gabojait.gabojaitspring.repository.profile.SkillRepository;
import com.gabojait.gabojaitspring.repository.profile.WorkRepository;
import com.gabojait.gabojaitspring.repository.review.ReviewRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.team.TeamRepository;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import com.gabojait.gabojaitspring.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.TESTER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevelopService {

    @Value("${api.name}")
    private String serverName;

    @PersistenceContext
    private final EntityManager entityManager;
    private final PasswordUtility passwordUtility;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final UserRoleRepository userRoleRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ReviewRepository reviewRepository;
    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillRepository skillRepository;
    private final WorkRepository workRepository;
    private final OfferRepository offerRepository;
    private final NotificationRepository notificationRepository;
    private final FcmRepository fcmRepository;
    private final FavoriteRepository favoriteRepository;

    /**
     * 서버명 조회
     * @return 서버명
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * 테스트 회원 조회 |
     * 404(TESTER_NOT_FOUND)
     * @param testerId 테스터 식별자
     * @return 회원 기본 응답
     */
    public String findTester(long testerId) {
        User user = findUser("tester" + testerId);

        return user.getUsername();
    }

    /**
     * 데이터베이스 초기화 및 테스트 데이터 주입
     */
    @Transactional
    public void resetAndInject() {
        resetDatabase();

        List<Contact> contacts = injectContacts();
        List<User> users = injectUsers(contacts);
        injectUserRoles(users);
        injectProfiles(users);
        injectCompleteTeams(users);
        injectCurrentTeams(users);
    }

    /**
     * 테스트 연락처 데이터 주입
     * @return 테스트 연락처들
     */
    @Transactional
    public List<Contact> injectContacts() {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Contact contact = Contact.builder()
                    .email("tester" + i + "@gabojait.com")
                    .verificationCode("000000")
                    .build();
            contact.verified();

            contactRepository.save(contact);
            contacts.add(contact);
        }

        return contacts;
    }

    /**
     * 테스트 회원 데이터 주입
     * @param contacts 테스트 연락처들
     * @return 테스트 회원들
     */
    @Transactional
    public List<User> injectUsers(List<Contact> contacts) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            User user = User.builder()
                    .username("tester" + (i + 1))
                    .password(passwordUtility.encodePassword("password" + (i + 1) + "!"))
                    .nickname("테스터" + (i + 1))
                    .gender(Gender.N)
                    .birthdate(LocalDate.of(2000, 1, (i % 30) + 1))
                    .lastRequestAt(LocalDateTime.now())
                    .contact(contacts.get(i))
                    .build();

            userRepository.save(user);
            users.add(user);
        }

        return users;
    }

    /**
     * 테스트 회원 권환 데이터 주입
     * @param users 테스트 회원들
     */
    @Transactional
    public void injectUserRoles(List<User> users) {
        users.forEach(user -> {
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(Role.USER)
                    .build();
            userRoleRepository.save(userRole);
        });
    }

    /**
     * 테스트 프로필 데이터 주입
     * @param users 테스트 회원들
     */
    @Transactional
    public void injectProfiles(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            Position position = Position.NONE;

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
                }

            users.get(i).updatePosition(position);
            users.get(i).updateProfileDescription("안녕하세요.");

            Education education = Education.builder()
                    .institutionName("가보자잇대")
                    .startedAt(LocalDate.of(2020, 3, 1))
                    .endedAt(LocalDate.of(2021, 12, 20))
                    .isCurrent(false)
                    .user(users.get(i))
                    .build();
            educationRepository.save(education);
            Portfolio portfolio = Portfolio.builder()
                    .portfolioName("깃허브")
                    .portfolioUrl("github.com/gabojait")
                    .media(Media.LINK)
                    .user(users.get(i))
                    .build();
            portfolioRepository.save(portfolio);
            Skill skill = Skill.builder()
                    .skillName("스프링")
                    .level(Level.MID)
                    .isExperienced(true)
                    .user(users.get(i))
                    .build();
            skillRepository.save(skill);
            Work work = Work.builder()
                    .corporationName("가보자잇사")
                    .workDescription("가보자잇사에서 백엔드 개발")
                    .startedAt(LocalDate.of(2020, 3, 1))
                    .endedAt(LocalDate.of(2021, 12, 20))
                    .isCurrent(false)
                    .user(users.get(i))
                    .build();
            workRepository.save(work);
        }
    }

    /**
     * 테스트 완료한 팀 데이터 주입
     * @param users 테스트 회원들
     */
    @Transactional
    public void injectCompleteTeams(List<User> users) {
        List<TeamMember> teamMembers = new ArrayList<>();
        for (int i = 0; i < 25; i += 5) {
            Team team = Team.builder()
                    .projectName("가볼까잇" + (i + 1))
                    .projectDescription("가볼까잇 프로젝트 설명입니다.")
                    .expectation("열정적인 팀원을 구합니다.")
                    .openChatUrl("https://open.kakao.com/o/test")
                    .designerMaxCnt((byte) 5)
                    .backendMaxCnt((byte) 5)
                    .frontendMaxCnt((byte) 5)
                    .managerMaxCnt((byte) 5)
                    .build();
            teamRepository.save(team);

            for (int j = i; j < i + 5; j++) {
                boolean isLeader = j % 5 == 0;
                TeamMember teamMember = TeamMember.builder()
                        .position(users.get(j).getPosition())
                        .isLeader(isLeader)
                        .user(users.get(j))
                        .team(team)
                        .build();

                teamMember.complete("github.com/gabojait", LocalDateTime.now());
                teamMemberRepository.save(teamMember);
                teamMembers.add(teamMember);
            }

            for (int j = i; j < i + 5; j++)
                for (int k = i; k < i + 5; k++) {
                    if (j == k) continue;

                    Review review = Review.builder()
                            .reviewee(teamMembers.get(j))
                            .reviewer(teamMembers.get(k))
                            .rating((byte) (new Random().nextInt(5) + 1))
                            .post("열정적인 팀원이였습니다.")
                            .build();
                    reviewRepository.save(review);
                }
        }
    }

    /**
     * 테스트 현재 팀 데이터 주입
     * @param users 테스트 회원들
     */
    @Transactional
    public void injectCurrentTeams(List<User> users) {
        for (int i = 0; i < 50; i++) {
            Team team = Team.builder()
                    .projectName("가보자잇" + (i + 1))
                    .projectDescription("가보자잇 프로젝트 설명입니다.")
                    .expectation("열정적인 팀원을 구합니다.")
                    .openChatUrl("https://open.kakao.com/o/test")
                    .designerMaxCnt((byte) 2)
                    .backendMaxCnt((byte) 2)
                    .frontendMaxCnt((byte) 2)
                    .managerMaxCnt((byte) 2)
                    .build();
            teamRepository.save(team);

            TeamMember teamMember = TeamMember.builder()
                    .position(users.get(i).getPosition())
                    .isLeader(true)
                    .user(users.get(i))
                    .team(team)
                    .build();
            teamMemberRepository.save(teamMember);
        }
    }

    /**
     * 데이터베이스 초기
     */
    @Transactional
    public void resetDatabase() {
        entityManager.createNativeQuery("DELETE FROM favorite").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM fcm").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM notification").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM offer").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM education").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM portfolio").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM skill").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM work").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM review").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM team_member").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM team").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM user_role").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM contact").executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE `favorite` AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE fcm AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE notification AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE offer AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE education AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE portfolio AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE skill AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE work AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE review AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE team_member AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE team AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE user_role AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE contact AUTO_INCREMENT = 1").executeUpdate();
    }

    /**
     * 회원 단건 조회 |
     * 404(TESTER_NOT_FOUND)
     * @param username 아이디
     * @return 회원
     */
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new CustomException(TESTER_NOT_FOUND);
                });
    }
}
