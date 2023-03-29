package com.inuappcenter.gabojaitspring.test.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
import com.inuappcenter.gabojaitspring.profile.domain.type.Level;
import com.inuappcenter.gabojaitspring.profile.domain.type.PortfolioType;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.inuappcenter.gabojaitspring.profile.repository.EducationRepository;
import com.inuappcenter.gabojaitspring.profile.repository.PortfolioRepository;
import com.inuappcenter.gabojaitspring.profile.repository.SkillRepository;
import com.inuappcenter.gabojaitspring.profile.repository.WorkRepository;
import com.inuappcenter.gabojaitspring.profile.service.EductionService;
import com.inuappcenter.gabojaitspring.profile.service.PortfolioService;
import com.inuappcenter.gabojaitspring.profile.service.SkillService;
import com.inuappcenter.gabojaitspring.profile.service.WorkService;
import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.domain.type.ReviewType;
import com.inuappcenter.gabojaitspring.review.repository.QuestionRepository;
import com.inuappcenter.gabojaitspring.review.repository.ReviewRepository;
import com.inuappcenter.gabojaitspring.review.service.QuestionService;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.offer.repository.OfferRepository;
import com.inuappcenter.gabojaitspring.team.repository.TeamRepository;
import com.inuappcenter.gabojaitspring.team.service.TeamService;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.domain.type.Gender;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import com.inuappcenter.gabojaitspring.user.service.ContactService;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestService {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillRepository skillRepository;
    private final WorkRepository workRepository;
    private final TeamRepository teamRepository;
    private final OfferRepository offerRepository;
    private final QuestionRepository questionRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;
    private final ContactService contactService;
    private final EductionService eductionService;
    private final PortfolioService portfolioService;
    private final SkillService skillService;
    private final WorkService workService;
    private final TeamService teamService;
    private final QuestionService questionService;

    /**
     * 데이터베이스 초기화 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void resetDatabase() {

        try {
            educationRepository.deleteAll();
            portfolioRepository.deleteAll();
            skillRepository.deleteAll();
            workRepository.deleteAll();
            teamRepository.deleteAll();
            offerRepository.deleteAll();
            userRepository.deleteAll();
            contactRepository.deleteAll();
            reviewRepository.deleteAll();
            questionRepository.deleteAll();

            injectUserData();
            injectProfileData();
            injectTeamData();
            injectQuestionData();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }
    }

    @Transactional
    public void injectUserData() {

        for (int i = 1; i <= 10; i++) {
            Contact contact = Contact.builder()
                    .email("test" + i + "@gabojait.com")
                    .verificationCode("000000")
                    .build();

            contact.verified();
            contact.registered();

            contactService.save(contact);

            User user = User.builder()
                    .username("test" + i)
                    .legalName("테스트")
                    .password(passwordEncoder.encode("password"))
                    .gender(Gender.MALE)
                    .birthdate(LocalDate.of(2000, 1, i))
                    .contact(contact)
                    .nickname("테스트" + i)
                    .roles(new ArrayList<>(List.of(Role.USER)))
                    .build();

            userService.save(user);
        }
    }

    @Transactional
    public void injectProfileData() {

        User user = userService.findOneByUsername("test1");

        user.updatePosition(Position.PM);
        userService.save(user);

        Skill skill = skillService.save(Skill.builder()
                .userId(user.getId())
                .skillName("기술명")
                .isExperienced(true)
                .level(Level.LOW)
                .build());
        userService.addSkill(user, skill);

        Education education = eductionService.save(Education.builder()
                .userId(user.getId())
                .institutionName("학교명")
                .startedDate(LocalDate.of(2020, 3, 1))
                .endedDate(LocalDate.of(2021, 12, 20))
                .isCurrent(false)
                .build());
        userService.addEducation(user, education);

        Work work = workService.save(Work.builder()
                .userId(user.getId())
                .corporationName("기관명")
                .startedDate(LocalDate.of(2020, 3, 1))
                .endedDate(LocalDate.of(2021, 12, 20))
                .isCurrent(false)
                .description("설명")
                .build());
        userService.addWork(user, work);

        Portfolio linkPortfolio = portfolioService.save(Portfolio.builder()
                .userId(user.getId())
                .portfolioType(PortfolioType.LINK)
                .name("링크 포트폴리오")
                .url("github.com/gs97ahn")
                .build());
        userService.addPortfolio(user, linkPortfolio);

        Portfolio filePortfolio = portfolioService.save(Portfolio.builder()
                .userId(user.getId())
                .portfolioType(PortfolioType.FILE)
                .name("파일 포트폴리오")
                .url("github.com/gs97ahn")
                .build());
        userService.addPortfolio(user, filePortfolio);
    }

    @Transactional
    public void injectTeamData() {
        User user = userService.findOneByUsername("test1");

        Team team = Team.builder()
                .projectName("Gabojait")
                .projectDescription("가보자잇 설명입니다.")
                .leaderUserId(user.getId())
                .designerTotalRecruitCnt(Short.valueOf("2"))
                .backendTotalRecruitCnt(Short.valueOf("2"))
                .frontendTotalRecruitCnt(Short.valueOf("2"))
                .projectManagerTotalRecruitCnt(Short.valueOf("2"))
                .expectation("열정적인 태도를 원해요.")
                .openChatUrl("https://open.kakao.com/o/test")
                .build();

        teamService.save(team);
        teamService.join(team, user, Position.PM.getType(), TeamMemberStatus.LEADER);
    }

    @Transactional
    public void injectQuestionData() {

        for (int i = 1; i <= 3; i++) {
            Question question;
            if (i == 3) {
                question = Question.builder()
                        .context("질문 " + i + "번입니다.")
                        .reviewType(ReviewType.ANSWER)
                        .build();
            } else {
                question = Question.builder()
                        .context("질문 " + i + "번입니다.")
                        .reviewType(ReviewType.RATING)
                        .build();
            }

            questionService.save(question);
        }
    }
}
