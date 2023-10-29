package com.gabojait.gabojaitspring.api.service.profile;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.profile.request.*;
import com.gabojait.gabojaitspring.api.dto.profile.response.PortfolioUrlResponse;
import com.gabojait.gabojaitspring.api.dto.profile.response.ProfileDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.profile.response.ProfileDetailResponse;
import com.gabojait.gabojaitspring.api.dto.profile.response.ProfileOfferResponse;
import com.gabojait.gabojaitspring.common.util.FileUtility;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.profile.*;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.domain.user.*;
import com.gabojait.gabojaitspring.exception.CustomException;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ProfileServiceTest {

    @Autowired private ProfileService profileService;
    @Autowired private EducationRepository educationRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private WorkRepository workRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private OfferRepository offerRepository;
    @Autowired private FileUtility fileUtility;

    @Test
    @DisplayName("내 프로필 조회를 한다.")
    void givenMyUserId_whenMyFindProfile_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com","tester1", "테스터일");

        Team completedTeam = createTeam("가보자잇1");
        teamRepository.save(completedTeam);
        TeamMember completedTeamMember1 = createTeamMember(user1, completedTeam);
        completedTeamMember1.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.save(completedTeamMember1);

        User user2 = createSavedDefaultUser("tester2@gabojait.com","tester2", "테스터이");

        Education education1 = createEducation("가보자잇대", 
                LocalDate.of(2001, 1, 1), user2);
        Education education2 = createEducation("가보자잇고", 
                LocalDate.of(2002, 1, 1), user2);
        educationRepository.saveAll(List.of(education1, education2));

        Portfolio portfolio1 = createPortfolio("깃허브", user2);
        Portfolio portfolio2 = createPortfolio("노션", user2);
        portfolioRepository.saveAll(List.of(portfolio1, portfolio2));

        Skill skill1 = createSkill("스프링", user2);
        Skill skill2 = createSkill("노드JS", user2);
        skillRepository.saveAll(List.of(skill1, skill2));

        Work work1 = createWork("가보자잇사", 
                LocalDate.of(2001, 1, 1), user2);
        Work work2 = createWork("가볼까잇사", 
                LocalDate.of(2002, 1, 1), user2);
        workRepository.saveAll(List.of(work1, work2));

        TeamMember completedTeamMember2 = createTeamMember(user2, completedTeam);
        completedTeamMember2.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.save(completedTeamMember2);
        completedTeam.complete("github.com/gabojait", LocalDateTime.now());
        teamRepository.save(completedTeam);

        Review review = createReview(completedTeamMember1, completedTeamMember2);
        reviewRepository.save(review);

        Team currentTeam = createTeam("가보자잇2");
        teamRepository.save(currentTeam);
        TeamMember currentTeamMember = createTeamMember(user2, currentTeam);
        teamMemberRepository.save(currentTeamMember);

        // when
        ProfileDefaultResponse response = profileService.findMyProfile(user2.getUsername());

        // then
        assertThat(userRepository.findById(user2.getId()).get().getVisitedCnt())
                .isEqualTo(0L);

        assertThat(response)
                .extracting("userId", "nickname", "position", "reviewCnt", "rating", "createdAt", "updatedAt",
                        "profileDescription", "imageUrl", "isLeader", "isSeekingTeam")
                .containsExactly(user2.getId(), user2.getNickname(), user2.getPosition(), user2.getReviewCnt(),
                        user2.getRating(), user2.getCreatedAt(), user2.getUpdatedAt(), user2.getProfileDescription(),
                        user2.getImageUrl(), currentTeamMember.getIsLeader(), user2.getIsSeekingTeam()
                );

        assertThat(response.getEducations())
                .extracting("educationId", "institutionName", "startedAt", "endedAt", "isCurrent", "createdAt",
                        "updatedAt")
                .containsExactly(
                        tuple(education2.getId(), education2.getInstitutionName(), education2.getStartedAt(),
                                education2.getEndedAt(), education2.getIsCurrent(), education2.getCreatedAt(),
                                education2.getUpdatedAt()),
                        tuple(education1.getId(), education1.getInstitutionName(), education1.getStartedAt(),
                                education1.getEndedAt(), education1.getIsCurrent(), education1.getCreatedAt(),
                                education1.getUpdatedAt())
                );

        assertThat(response.getPortfolios())
                .extracting("portfolioId", "portfolioName", "portfolioUrl", "media", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(portfolio2.getId(), portfolio2.getPortfolioName(), portfolio2.getPortfolioUrl(),
                                portfolio2.getMedia(), portfolio2.getCreatedAt(), portfolio2.getUpdatedAt()),
                        tuple(portfolio1.getId(), portfolio1.getPortfolioName(), portfolio1.getPortfolioUrl(),
                                portfolio1.getMedia(), portfolio1.getCreatedAt(), portfolio1.getUpdatedAt())
                        );

        assertThat(response.getSkills())
                .extracting("skillId", "skillName", "isExperienced", "level", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(skill2.getId(), skill2.getSkillName(), skill2.getIsExperienced(), skill2.getLevel(),
                                skill2.getCreatedAt(), skill2.getUpdatedAt()),
                        tuple(skill1.getId(), skill1.getSkillName(), skill1.getIsExperienced(), skill1.getLevel(),
                                skill1.getCreatedAt(), skill1.getUpdatedAt())
                );

        assertThat(response.getWorks())
                .extracting("workId", "corporationName", "workDescription", "startedAt", "endedAt", "isCurrent",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(work2.getId(), work2.getCorporationName(), work2.getWorkDescription(),
                                work2.getStartedAt(), work2.getEndedAt(), work2.getIsCurrent(), work2.getCreatedAt(),
                                work2.getUpdatedAt()),
                        tuple(work1.getId(), work1.getCorporationName(), work1.getWorkDescription(),
                                work1.getStartedAt(), work1.getEndedAt(), work1.getIsCurrent(), work1.getCreatedAt(),
                                work1.getUpdatedAt())
                );

        assertThat(response.getCompletedTeams())
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(completedTeam.getId(), completedTeam.getProjectName(),
                                completedTeam.getDesignerCurrentCnt(), completedTeam.getBackendCurrentCnt(),
                                completedTeam.getFrontendCurrentCnt(), completedTeam.getManagerCurrentCnt(),
                                completedTeam.getDesignerMaxCnt(), completedTeam.getBackendMaxCnt(),
                                completedTeam.getFrontendMaxCnt(), completedTeam.getManagerMaxCnt(),
                                completedTeam.getCreatedAt(), completedTeam.getUpdatedAt()));

        assertThat(response.getCurrentTeam())
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(currentTeam.getId(), currentTeam.getProjectName(), currentTeam.getDesignerCurrentCnt(),
                        currentTeam.getBackendCurrentCnt(), currentTeam.getFrontendCurrentCnt(),
                        currentTeam.getManagerCurrentCnt(), currentTeam.getDesignerMaxCnt(),
                        currentTeam.getBackendMaxCnt(), currentTeam.getFrontendMaxCnt(), currentTeam.getManagerMaxCnt(),
                        currentTeam.getCreatedAt(), currentTeam.getUpdatedAt());

                assertThat(response.getReviews())
                .extracting("reviewId", "reviewer", "rating", "post", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(review.getId(), "익명1", review.getRating(), review.getPost(), review.getCreatedAt(),
                                review.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 프로필 조회시 예외가 발생한다.")
    void givenNonExistingUser_whenMyFindProfile_thenThrow() {
        // given
        String username = "tester";

        // when & then
        assertThatThrownBy(() -> profileService.findMyProfile(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("다른 회원 식별자로 다른 프로필 조회를 한다.")
    void givenOtherUserId_whenFindOtherProfile_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com","tester1", "테스터일");

        Team completedTeam = createTeam("가보자잇1");
        teamRepository.save(completedTeam);
        TeamMember completedTeamMember1 = createTeamMember(user1, completedTeam);
        completedTeamMember1.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.save(completedTeamMember1);

        User user2 = createSavedDefaultUser("tester2@gabojait.com","tester2", "테스터이");

        Education education1 = createEducation("가보자잇대",
                LocalDate.of(2001, 1, 1), user2);
        Education education2 = createEducation("가보자잇고",
                LocalDate.of(2002, 1, 1), user2);
        educationRepository.saveAll(List.of(education1, education2));

        Portfolio portfolio1 = createPortfolio("깃허브", user2);
        Portfolio portfolio2 = createPortfolio("노션", user2);
        portfolioRepository.saveAll(List.of(portfolio1, portfolio2));

        Skill skill1 = createSkill("스프링", user2);
        Skill skill2 = createSkill("노드JS", user2);
        skillRepository.saveAll(List.of(skill1, skill2));

        Work work1 = createWork("가보자잇사",
                LocalDate.of(2001, 1, 1), user2);
        Work work2 = createWork("가볼까잇사",
                LocalDate.of(2002, 1, 1), user2);
        workRepository.saveAll(List.of(work1, work2));

        TeamMember completedTeamMember2 = createTeamMember(user2, completedTeam);
        completedTeamMember2.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.save(completedTeamMember2);
        completedTeam.complete("github.com/gabojait", LocalDateTime.now());
        teamRepository.save(completedTeam);

        Review review = createReview(completedTeamMember1, completedTeamMember2);
        reviewRepository.save(review);

        Team currentTeam = createTeam("가보자잇2");
        teamRepository.save(currentTeam);
        TeamMember currentTeamMember = createTeamMember(user2, currentTeam);
        teamMemberRepository.save(currentTeamMember);

        // when
        ProfileDetailResponse response = profileService.findOtherProfile(user1.getUsername(), user2.getId());

        // then
        assertThat(userRepository.findById(user2.getId()).get().getVisitedCnt())
                .isEqualTo(1L);

        assertThat(response)
                .extracting("userId", "nickname", "position", "reviewCnt",
                        "rating", "createdAt", "updatedAt", "profileDescription",
                        "imageUrl", "isLeader", "isSeekingTeam")
                .containsExactly(user2.getId(), user2.getNickname(), user2.getPosition(), user2.getReviewCnt(),
                        user2.getRating(), user2.getCreatedAt(), user2.getUpdatedAt(), user2.getProfileDescription(),
                        user2.getImageUrl(), currentTeamMember.getIsLeader(), user2.getIsSeekingTeam()
                );

        assertThat(response.getEducations())
                .extracting("educationId", "institutionName",
                        "startedAt", "endedAt", "isCurrent",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(education2.getId(), education2.getInstitutionName(),
                                education2.getStartedAt(), education2.getEndedAt(), education2.getIsCurrent(),
                                education2.getCreatedAt(), education2.getUpdatedAt()),
                        tuple(education1.getId(), education1.getInstitutionName(),
                                education1.getStartedAt(), education1.getEndedAt(), education1.getIsCurrent(),
                                education1.getCreatedAt(), education1.getUpdatedAt())
                );

        assertThat(response.getPortfolios())
                .extracting("portfolioId", "portfolioName",
                        "portfolioUrl", "media", "createdAt",
                        "updatedAt")
                .containsExactly(
                        tuple(portfolio2.getId(), portfolio2.getPortfolioName(),
                                portfolio2.getPortfolioUrl(), portfolio2.getMedia(), portfolio2.getCreatedAt(),
                                portfolio2.getUpdatedAt()),
                        tuple(portfolio1.getId(), portfolio1.getPortfolioName(),
                                portfolio1.getPortfolioUrl(), portfolio1.getMedia(), portfolio1.getCreatedAt(),
                                portfolio1.getUpdatedAt())
                );

        assertThat(response.getSkills())
                .extracting("skillId", "skillName", "isExperienced",
                        "level", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(skill2.getId(), skill2.getSkillName(), skill2.getIsExperienced(),
                                skill2.getLevel(), skill2.getCreatedAt(), skill2.getUpdatedAt()),
                        tuple(skill1.getId(), skill1.getSkillName(), skill1.getIsExperienced(),
                                skill1.getLevel(), skill1.getCreatedAt(), skill1.getUpdatedAt())
                );

        assertThat(response.getWorks())
                .extracting("workId", "corporationName", "workDescription",
                        "startedAt", "endedAt", "isCurrent", "createdAt",
                        "updatedAt")
                .containsExactly(
                        tuple(work2.getId(), work2.getCorporationName(), work2.getWorkDescription(),
                                work2.getStartedAt(), work2.getEndedAt(), work2.getIsCurrent(), work2.getCreatedAt(),
                                work2.getUpdatedAt()),
                        tuple(work1.getId(), work1.getCorporationName(), work1.getWorkDescription(),
                                work1.getStartedAt(), work1.getEndedAt(), work1.getIsCurrent(), work1.getCreatedAt(),
                                work1.getUpdatedAt())
                );

        assertThat(response.getCompletedTeams())
                .extracting("teamId", "projectName",
                        "designerCurrentCnt", "backendCurrentCnt",
                        "frontendCurrentCnt", "managerCurrentCnt",
                        "designerMaxCnt", "backendMaxCnt",
                        "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(completedTeam.getId(), completedTeam.getProjectName(),
                                completedTeam.getDesignerCurrentCnt(), completedTeam.getBackendCurrentCnt(),
                                completedTeam.getFrontendCurrentCnt(), completedTeam.getManagerCurrentCnt(),
                                completedTeam.getDesignerMaxCnt(), completedTeam.getBackendMaxCnt(),
                                completedTeam.getFrontendMaxCnt(), completedTeam.getManagerMaxCnt(),
                                completedTeam.getCreatedAt(), completedTeam.getUpdatedAt()));

        assertThat(response.getCurrentTeam())
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(currentTeam.getId(), currentTeam.getProjectName(),
                        currentTeam.getDesignerCurrentCnt(), currentTeam.getBackendCurrentCnt(),
                        currentTeam.getFrontendCurrentCnt(), currentTeam.getManagerCurrentCnt(),
                        currentTeam.getDesignerMaxCnt(), currentTeam.getBackendMaxCnt(),
                        currentTeam.getFrontendMaxCnt(), currentTeam.getManagerMaxCnt(),
                        currentTeam.getCreatedAt(), currentTeam.getUpdatedAt());

        assertThat(response.getReviews())
                .extracting("reviewId", "reviewer", "rating", "post", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(review.getId(), "익명1", review.getRating(), review.getPost(),
                                review.getCreatedAt(), review.getUpdatedAt())
                );

        assertThat(response.getOffers()).isEmpty();

        assertThat(response.getIsFavorite()).isFalse();
    }

    @Test
    @DisplayName("내 회원 식별자로 다른 프로필 조회를 한다.")
    void givenMyUserId_whenFindOtherProfile_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com","tester1", "테스터일");

        Team completedTeam = createTeam("가보자잇1");
        teamRepository.save(completedTeam);
        TeamMember completedTeamMember1 = createTeamMember(user1, completedTeam);
        completedTeamMember1.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.save(completedTeamMember1);

        User user2 = createSavedDefaultUser("tester2@gabojait.com","tester2", "테스터이");

        Education education1 = createEducation("가보자잇대",
                LocalDate.of(2001, 1, 1), user2);
        Education education2 = createEducation("가보자잇고",
                LocalDate.of(2002, 1, 1), user2);
        educationRepository.saveAll(List.of(education1, education2));

        Portfolio portfolio1 = createPortfolio("깃허브", user2);
        Portfolio portfolio2 = createPortfolio("노션", user2);
        portfolioRepository.saveAll(List.of(portfolio1, portfolio2));

        Skill skill1 = createSkill("스프링", user2);
        Skill skill2 = createSkill("노드JS", user2);
        skillRepository.saveAll(List.of(skill1, skill2));

        Work work1 = createWork("가보자잇사",
                LocalDate.of(2001, 1, 1), user2);
        Work work2 = createWork("가볼까잇사",
                LocalDate.of(2002, 1, 1), user2);
        workRepository.saveAll(List.of(work1, work2));

        TeamMember completedTeamMember2 = createTeamMember(user2, completedTeam);
        completedTeamMember2.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.save(completedTeamMember2);
        completedTeam.complete("github.com/gabojait", LocalDateTime.now());
        teamRepository.save(completedTeam);

        Review review = createReview(completedTeamMember1, completedTeamMember2);
        reviewRepository.save(review);

        Team currentTeam = createTeam("가보자잇2");
        teamRepository.save(currentTeam);
        TeamMember currentTeamMember = createTeamMember(user2, currentTeam);
        teamMemberRepository.save(currentTeamMember);

        // when
        ProfileDetailResponse response = profileService.findOtherProfile(user2.getUsername(), user2.getId());

        // then
        assertThat(userRepository.findById(user2.getId()).get().getVisitedCnt())
                .isEqualTo(0L);

        assertThat(response)
                .extracting("userId", "nickname", "position", "reviewCnt",
                        "rating", "createdAt", "updatedAt", "profileDescription",
                        "imageUrl", "isLeader", "isSeekingTeam")
                .containsExactly(user2.getId(), user2.getNickname(), user2.getPosition(), user2.getReviewCnt(),
                        user2.getRating(), user2.getCreatedAt(), user2.getUpdatedAt(), user2.getProfileDescription(),
                        user2.getImageUrl(), currentTeamMember.getIsLeader(), user2.getIsSeekingTeam()
                );

        assertThat(response.getEducations())
                .extracting("educationId", "institutionName",
                        "startedAt", "endedAt", "isCurrent",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(education2.getId(), education2.getInstitutionName(),
                                education2.getStartedAt(), education2.getEndedAt(), education2.getIsCurrent(),
                                education2.getCreatedAt(), education2.getUpdatedAt()),
                        tuple(education1.getId(), education1.getInstitutionName(),
                                education1.getStartedAt(), education1.getEndedAt(), education1.getIsCurrent(),
                                education1.getCreatedAt(), education1.getUpdatedAt())
                );

        assertThat(response.getPortfolios())
                .extracting("portfolioId", "portfolioName",
                        "portfolioUrl", "media", "createdAt",
                        "updatedAt")
                .containsExactly(
                        tuple(portfolio2.getId(), portfolio2.getPortfolioName(),
                                portfolio2.getPortfolioUrl(), portfolio2.getMedia(), portfolio2.getCreatedAt(),
                                portfolio2.getUpdatedAt()),
                        tuple(portfolio1.getId(), portfolio1.getPortfolioName(),
                                portfolio1.getPortfolioUrl(), portfolio1.getMedia(), portfolio1.getCreatedAt(),
                                portfolio1.getUpdatedAt())
                );

        assertThat(response.getSkills())
                .extracting("skillId", "skillName", "isExperienced",
                        "level", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(skill2.getId(), skill2.getSkillName(), skill2.getIsExperienced(),
                                skill2.getLevel(), skill2.getCreatedAt(), skill2.getUpdatedAt()),
                        tuple(skill1.getId(), skill1.getSkillName(), skill1.getIsExperienced(),
                                skill1.getLevel(), skill1.getCreatedAt(), skill1.getUpdatedAt())
                );

        assertThat(response.getWorks())
                .extracting("workId", "corporationName", "workDescription",
                        "startedAt", "endedAt", "isCurrent", "createdAt",
                        "updatedAt")
                .containsExactly(
                        tuple(work2.getId(), work2.getCorporationName(), work2.getWorkDescription(),
                                work2.getStartedAt(), work2.getEndedAt(), work2.getIsCurrent(), work2.getCreatedAt(),
                                work2.getUpdatedAt()),
                        tuple(work1.getId(), work1.getCorporationName(), work1.getWorkDescription(),
                                work1.getStartedAt(), work1.getEndedAt(), work1.getIsCurrent(), work1.getCreatedAt(),
                                work1.getUpdatedAt())
                );

        assertThat(response.getCompletedTeams())
                .extracting("teamId", "projectName",
                        "designerCurrentCnt", "backendCurrentCnt",
                        "frontendCurrentCnt", "managerCurrentCnt",
                        "designerMaxCnt", "backendMaxCnt",
                        "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(completedTeam.getId(), completedTeam.getProjectName(),
                                completedTeam.getDesignerCurrentCnt(), completedTeam.getBackendCurrentCnt(),
                                completedTeam.getFrontendCurrentCnt(), completedTeam.getManagerCurrentCnt(),
                                completedTeam.getDesignerMaxCnt(), completedTeam.getBackendMaxCnt(),
                                completedTeam.getFrontendMaxCnt(), completedTeam.getManagerMaxCnt(),
                                completedTeam.getCreatedAt(), completedTeam.getUpdatedAt()));

        assertThat(response.getCurrentTeam())
                .extracting("teamId", "projectName",
                        "designerCurrentCnt", "backendCurrentCnt",
                        "frontendCurrentCnt", "managerCurrentCnt",
                        "designerMaxCnt", "backendMaxCnt",
                        "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(currentTeam.getId(), currentTeam.getProjectName(),
                        currentTeam.getDesignerCurrentCnt(), currentTeam.getBackendCurrentCnt(),
                        currentTeam.getFrontendCurrentCnt(), currentTeam.getManagerCurrentCnt(),
                        currentTeam.getDesignerMaxCnt(), currentTeam.getBackendMaxCnt(),
                        currentTeam.getFrontendMaxCnt(), currentTeam.getManagerMaxCnt(),
                        currentTeam.getCreatedAt(), currentTeam.getUpdatedAt());

        assertThat(response.getReviews())
                .extracting("reviewId", "reviewer", "rating", "post",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(review.getId(), "익명1", review.getRating(), review.getPost(),
                                review.getCreatedAt(), review.getUpdatedAt())
                );

        assertThat(response.getOffers()).isEmpty();

        assertThat(response.getIsFavorite()).isNull();
    }

    @Test
    @DisplayName("존재하지 않은 회원 식별자로 다른 프로필 조회시 예외가 발생한다.")
    void givenNonExistingUserId_whenFindOtherProfile_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com","tester", "테스터");
        long userId = 2L;

        // when & then
        assertThatThrownBy(() -> profileService.findOtherProfile(user.getUsername(), userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 다른 프로필 조회시 예외가 발생한다.")
    void givenNonExistingUsername_whenFindOtherProfile_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com","tester", "테스터일");
        String username = "테스터이";

        // when & then
        assertThatThrownBy(() -> profileService.findOtherProfile(username, user.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("프로필 이미지 업로드를 한다.")
    void givenValid_whenUploadProfileImage_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        MultipartFile image = new MockMultipartFile("data", "filename.txt", "image/png", "image".getBytes());

        // when
        ProfileDefaultResponse response = profileService.uploadProfileImage(user.getUsername(), image);

        // then
        assertThat(response.getImageUrl()).isNotBlank();
    }

    @Test
    @DisplayName("파일이 없이 프로필 이미지 업로드시 예외가 발생한다.")
    void givenBlankImage_whenUploadProfileImage_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        MultipartFile image = null;

        // when & then
        assertThatThrownBy(() -> profileService.uploadProfileImage(user.getUsername(), image))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(FILE_FIELD_REQUIRED);
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 프로필 이미지 업로드시 예외가 발생한다.")
    void givenNonExistingUser_whenUploadProfileImage_thenThrow() {
        // given
        String username = "tester";

        MultipartFile image = new MockMultipartFile("data", "filename.txt", "image/png", "image".getBytes());

        // when & then
        assertThatThrownBy(() -> profileService.uploadProfileImage(username, image))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("올바르지 않은 이미지 타입으로 프로필 이미지 업로드시 예외가 발생한다.")
    void givenUnsupportedImageType_whenUploadProfileImage_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        MultipartFile image = new MockMultipartFile("data", "filename.txt", "image/txt", "image".getBytes());

        // when & then
        assertThatThrownBy(() -> profileService.uploadProfileImage(user.getUsername(), image))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(IMAGE_TYPE_UNSUPPORTED);
    }

    @Test
    @DisplayName("프로필 이미지를 삭제한다.")
    void givenUsername_whenDeleteProfileImage_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        // when
        ProfileDefaultResponse response = profileService.deleteProfileImage(user.getUsername());

        // then
        assertThat(response.getImageUrl()).isNull();
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 프로필 이미지를 삭제시 예외가 발생한다.")
    void givenNonExistingUser_whenDeleteProfileImage_thenThrow() {
        // given
        String username = "tester";

        // when & then
        assertThatThrownBy(() -> profileService.deleteProfileImage(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("팀 찾기 여부 업데이트를 한다.")
    void givenValid_whenUpdateIsSeekingTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        boolean isSeekingTeam = false;

        // when
        profileService.updateIsSeekingTeam(user.getUsername(), isSeekingTeam);

        // then
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        assertThat(foundUser.get().getIsSeekingTeam()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 팀 찾기 여부 업데이트시 예외가 발생한다.")
    void givenNonExistingUser_whenUpdateIsSeekingTeam_thenThrow() {
        // given
        String username = "tester";
        boolean isSeekingTeam = false;

        // when & then
        assertThatThrownBy(() -> profileService.updateIsSeekingTeam(username, isSeekingTeam))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("프로필 자기소개 업데이트를 한다.")
    void givenValid_whenUpdateProfileDescription_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        String profileDescription = "안녕하세요.";

        // when
        profileService.updateProfileDescription(user.getUsername(), profileDescription);

        // then
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        assertEquals(profileDescription, foundUser.get().getProfileDescription());
    }

    @Test
    @DisplayName("존재하지 않은 아이디로 프로필 자기소개 업데이트시 예외가 발생한다.")
    void givenNonExistingUser_whenUpdateProfileDescription_thenThrow() {
        // given
        String username = "tester";
        String profileDescription = "안녕하세요.";

        // when & then
        assertThatThrownBy(() -> profileService.updateProfileDescription(username, profileDescription))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("학력들을 생성 수정 및 삭제를 한다.")
    void givenValid_whenUpdateEducations_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Education education1 = createEducation("가보자잇중", LocalDate.of(2000, 1, 1), user);
        Education education2 = createEducation("가보자잇고", LocalDate.of(2001, 1, 1), user);
        Education education3 = createEducation("가보자잇대", LocalDate.of(2002, 1, 1), user);
        educationRepository.saveAll(List.of(education1, education2, education3));

        EducationDefaultRequest request1 = createEducationDefaultRequest(education2.getId(),
                education2.getInstitutionName(), education2.getStartedAt(), education2.getEndedAt(),
                education2.getIsCurrent());
        EducationDefaultRequest request2 = createEducationDefaultRequest(education3.getId(), "가볼까잇대",
                LocalDate.of(2003, 1, 1), LocalDate.of(2004, 1, 1), false);
        EducationDefaultRequest request3 = createEducationDefaultRequest(null, "가보자잇대학원",
                LocalDate.of(2004, 1, 1), null, true);

        List<EducationDefaultRequest> requests = new ArrayList<>(List.of(request1, request2, request3));

        // when
        profileService.updateEducations(user, requests);

        // then
        List<Education> educations = educationRepository.findAll(user.getId());

        assertThat(educations)
                .extracting("institutionName", "startedAt", "endedAt", "isCurrent")
                .containsExactly(
                        tuple(request3.getInstitutionName(), request3.getStartedAt(),
                                request3.getEndedAt(), request3.getIsCurrent()),
                        tuple(request2.getInstitutionName(), request2.getStartedAt(),
                                request2.getEndedAt(), request2.getIsCurrent()),
                        tuple(request1.getInstitutionName(), request1.getStartedAt(),
                                request1.getEndedAt(), request1.getIsCurrent())
                );
    }

    @Test
    @DisplayName("포트폴리오들을 생성 수정 및 삭제를 한다.")
    void givenValid_whenUpdatePortfolios_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Portfolio portfolio1 = createPortfolio("포트폴리오1", user);
        Portfolio portfolio2 = createPortfolio("포트폴리오2", user);
        Portfolio portfolio3 = createPortfolio("포트폴리오3", user);
        portfolioRepository.saveAll(List.of(portfolio1, portfolio2, portfolio3));

        PortfolioDefaultRequest request1 = createPortfolioDefaultRequest(portfolio2.getId(),
                portfolio2.getPortfolioName(), portfolio2.getPortfolioUrl(), portfolio2.getMedia().toString());
        PortfolioDefaultRequest request2 = createPortfolioDefaultRequest(portfolio3.getId(), "포트폴리오4",
                "github.com/gabojait1", Media.FILE.toString());
        PortfolioDefaultRequest request3 = createPortfolioDefaultRequest(null, "포트폴리오5",
                "github.com/gabojait2", Media.LINK.toString());

        List<PortfolioDefaultRequest> requests = new ArrayList<>(List.of(request1, request2, request3));

        // when
        profileService.updatePortfolios(user, requests);

        // then
        List<Portfolio> portfolios = portfolioRepository.findAll(user.getId());

        assertThat(portfolios)
                .extracting("portfolioName", "portfolioUrl", "media")
                .containsExactly(
                        tuple(request3.getPortfolioName(), request3.getPortfolioUrl(), Media.valueOf(request3.getMedia())),
                        tuple(request2.getPortfolioName(), request2.getPortfolioUrl(), Media.valueOf(request2.getMedia())),
                        tuple(request1.getPortfolioName(), request1.getPortfolioUrl(), Media.valueOf(request1.getMedia()))
                );
    }

    @Test
    @DisplayName("기술들을 생성 수정 및 삭제를 한다.")
    void givenValid_whenUpdateSkills_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Skill skill1 = createSkill("스킬1", user);
        Skill skill2 = createSkill("스킬2", user);
        Skill skill3 = createSkill("스킬3", user);
        skillRepository.saveAll(List.of(skill1, skill2, skill3));

        SkillDefaultRequest request1 = createSkillDefaultRequest(skill2.getId(), skill2.getSkillName(),
                skill2.getIsExperienced(), skill2.getLevel().toString());
        SkillDefaultRequest request2 = createSkillDefaultRequest(skill3.getId(), "스킬4", false, Level.LOW.toString());
        SkillDefaultRequest request3 = createSkillDefaultRequest(null, "스킬4", true, Level.HIGH.toString());

        List<SkillDefaultRequest> requests = new ArrayList<>(List.of(request1, request2, request3));

        // when
        profileService.updateSkills(user, requests);

        // then
        List<Skill> skills = skillRepository.findAll(user.getId());

        assertThat(skills)
                .extracting("skillName", "level", "isExperienced")
                .containsExactly(
                        tuple(request3.getSkillName(), Level.valueOf(request3.getLevel()), request3.getIsExperienced()),
                        tuple(request2.getSkillName(), Level.valueOf(request2.getLevel()), request2.getIsExperienced()),
                        tuple(request1.getSkillName(), Level.valueOf(request1.getLevel()), request1.getIsExperienced())
                );
    }

    @Test
    @DisplayName("경력들을 생성 수정 및 삭제를 한다.")
    void givenValid_whenUpdateWorks_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Work work1 = createWork("경력1", LocalDate.of(2000, 1, 1), user);
        Work work2 = createWork("경력2", LocalDate.of(2001, 1, 1), user);
        Work work3 = createWork("경력3", LocalDate.of(2002, 1, 1), user);
        workRepository.saveAll(List.of(work1, work2, work3));

        WorkDefaultRequest request1 = createWorkDefaultRequest(work2.getId(), work2.getCorporationName(),
                work2.getWorkDescription(), work2.getStartedAt(), work2.getEndedAt(), work2.getIsCurrent());
        WorkDefaultRequest request2 = createWorkDefaultRequest(work3.getId(), "경력4", "경력4", LocalDate.of(2003, 1, 1),
                LocalDate.of(2004, 1, 1), false);
        WorkDefaultRequest request3 = createWorkDefaultRequest(null, "경력5", "경력5", LocalDate.of(2004, 1, 1),
                LocalDate.of(2005, 1, 1), false);

        List<WorkDefaultRequest> requests = new ArrayList<>(List.of(request1, request2, request3));

        // when
        profileService.updateWorks(user, requests);

        // then
        List<Work> works = workRepository.findAll(user.getId());

        assertThat(works)
                .extracting("corporationName", "workDescription", "startedAt", "endedAt", "isCurrent")
                .containsExactly(
                        tuple(request3.getCorporationName(), request3.getWorkDescription(), request3.getStartedAt(),
                                request3.getEndedAt(), request3.getIsCurrent()),
                        tuple(request2.getCorporationName(), request2.getWorkDescription(), request2.getStartedAt(),
                                request2.getEndedAt(), request2.getIsCurrent()),
                        tuple(request1.getCorporationName(), request1.getWorkDescription(), request1.getStartedAt(),
                                request1.getEndedAt(), request1.getIsCurrent())
                );
    }

    @Test
    @DisplayName("프로필 업데이틀를 한다.")
    void givenValid_whenUpdateProfile_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        ProfileDefaultRequest request = createValidProfileDefaultRequest();

        // when
        ProfileDefaultResponse response = profileService.updateProfile(user.getUsername(), request);

        // then
        assertThat(response.getPosition().toString()).isEqualTo(request.getPosition());

        assertThat(response.getEducations())
                .extracting("institutionName", "startedAt", "endedAt", "isCurrent")
                .containsExactly(
                        tuple(request.getEducations().get(0).getInstitutionName(),
                                request.getEducations().get(0).getStartedAt(),
                                request.getEducations().get(0).getEndedAt(),
                                request.getEducations().get(0).getIsCurrent())
                );

        assertThat(response.getPortfolios())
                .extracting("portfolioName", "portfolioUrl", "media")
                .containsExactly(
                        tuple(request.getPortfolios().get(0).getPortfolioName(),
                                request.getPortfolios().get(0).getPortfolioUrl(),
                                Media.valueOf(request.getPortfolios().get(0).getMedia()))
                );

        assertThat(response.getSkills())
                .extracting("skillName", "isExperienced", "level")
                .containsExactly(
                        tuple(request.getSkills().get(0).getSkillName(),
                                request.getSkills().get(0).getIsExperienced(),
                                Level.valueOf(request.getSkills().get(0).getLevel()))
                );

        assertThat(response.getWorks())
                .extracting("corporationName", "workDescription", "startedAt", "endedAt", "isCurrent")
                .containsExactly(
                        tuple(request.getWorks().get(0).getCorporationName(),
                                request.getWorks().get(0).getWorkDescription(),
                                request.getWorks().get(0).getStartedAt(),
                                request.getWorks().get(0).getEndedAt(),
                                request.getWorks().get(0).getIsCurrent())
                );
    }

    @Test
    @DisplayName("올바르지 않은 학력 날짜로 프로필 업데이트시 예외가 발생한다.")
    void givenInvalidEducationDate_whenUpdateProfile_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        ProfileDefaultRequest request = createValidProfileDefaultRequest();
        request.getEducations().get(0).setStartedAt(LocalDate.of(2001, 1, 1));
        request.getEducations().get(0).setEndedAt(LocalDate.of(2000, 1, 1));
        request.getEducations().get(0).setIsCurrent(false);

        // when & then
        assertThatThrownBy(() -> profileService.updateProfile(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EDUCATION_DATE_INVALID);
    }

    @Test
    @DisplayName("미입력된 학력 종료일로 프로필 업데이트시 예외가 발생한다.")
    void givenBlankEducationEndedAt_whenUpdateProfile_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        ProfileDefaultRequest request = createValidProfileDefaultRequest();
        request.getEducations().get(0).setStartedAt(LocalDate.of(2001, 1, 1));
        request.getEducations().get(0).setEndedAt(null);
        request.getEducations().get(0).setIsCurrent(false);

        // when & then
        assertThatThrownBy(() -> profileService.updateProfile(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EDUCATION_ENDED_AT_FIELD_REQUIRED);
    }

    @Test
    @DisplayName("올바르지 않은 경력 날짜로 프로필 업데이트시 예외가 발생한다.")
    void givenInvalidWorkDate_whenUpdateProfile_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        ProfileDefaultRequest request = createValidProfileDefaultRequest();
        request.getWorks().get(0).setStartedAt(LocalDate.of(2001, 1, 1));
        request.getWorks().get(0).setEndedAt(LocalDate.of(2000, 1, 1));
        request.getWorks().get(0).setIsCurrent(false);

        // when & then
        assertThatThrownBy(() -> profileService.updateProfile(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(WORK_DATE_INVALID);
    }

    @Test
    @DisplayName("미입력된 경력 종료일로 프로필 업데이트시 예외가 발생한다.")
    void givenBlankWorkEndedAt_whenUpdateProfile_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        ProfileDefaultRequest request = createValidProfileDefaultRequest();
        request.getWorks().get(0).setStartedAt(LocalDate.of(2001, 1, 1));
        request.getWorks().get(0).setEndedAt(null);
        request.getWorks().get(0).setIsCurrent(false);

        // when & then
        assertThatThrownBy(() -> profileService.updateProfile(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(WORK_ENDED_AT_FIELD_REQUIRED);
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 프로필 업데이트시 예외가 발생한다.")
    void givenNonExistingUsername_whenUpdateProfile_thenThrow() {
        // given
        String username = "tester";

        ProfileDefaultRequest request = createValidProfileDefaultRequest();

        // when & then
        assertThatThrownBy(() -> profileService.updateProfile(username, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("포트폴리오 파일을 업로드한다.")
    void givenValid_whenUploadPortfolioFile_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        MultipartFile file = new MockMultipartFile("data", "filename.txt", "image/png", "image".getBytes());

        // when
        PortfolioUrlResponse response = profileService.uploadPortfolioFile(user.getUsername(), file);

        // then
        assertThat(response.getPortfolioUrl()).isNotBlank();
    }

    @Test
    @DisplayName("파일이 없이 포트폴리오 파일 업로드시 예외가 발생한다.")
    void givenBlankFile_whenUploadPortfolioFile_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        MultipartFile file = null;

        // when & then
        assertThatThrownBy(() -> profileService.uploadPortfolioFile(user.getUsername(), file))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(FILE_FIELD_REQUIRED);
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 포트폴리오 파일 업로드시 예외가 발생한다.")
    void givenNonExistingUser_whenUploadPortfolioFile_thenThrow() {
        // given
        String username = "tester";

        MultipartFile file = new MockMultipartFile("data", "filename.txt", "image/png", "image".getBytes());

        // when & then
        assertThatThrownBy(() -> profileService.uploadPortfolioFile(username, file))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("올바르지 않은 파일 타입으로 포트폴리오 파일 업로드시 예외가 발생한다.")
    void givenUnsupportedFileType_whenUploadPortfolioFile_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        MultipartFile file = new MockMultipartFile("data", "filename.txt", "image/gif", "image".getBytes());

        // when & then
        assertThatThrownBy(() -> profileService.uploadPortfolioFile(user.getUsername(), file))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(FILE_TYPE_UNSUPPORTED);
    }

    @Test
    @DisplayName("프로필 페이징 조회를 한다.")
    void givenValid_whenFindPageUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테서티일");
        Team team = createTeam("가보자잇");
        teamRepository.save(team);
        TeamMember teamMember = createTeamMember(user1, team);
        teamMemberRepository.save(teamMember);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테서티이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테서티삼");
        User user4 = createSavedDefaultUser("tester4@gabojait.com", "tester4", "테서티사");

        Offer offer1 = createOffer(user2, team);
        Offer offer2 = createOffer(user3, team);
        Offer offer3 = createOffer(user4, team);
        offerRepository.saveAll(List.of(offer1, offer2, offer3));

        Position position = Position.NONE;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<ProfileOfferResponse>> users = profileService.findPageUser(user1.getUsername(), position,
                pageFrom, pageSize);

        // then
        assertThat(users.getData())
                .extracting("userId", "nickname", "position")
                .containsExactly(
                        tuple(user4.getId(), user4.getNickname(), user4.getPosition()),
                        tuple(user3.getId(), user3.getNickname(), user3.getPosition())
                );

        assertThat(users.getData().get(0).getOffers())
                .extracting("offerId", "position", "isAccepted", "offeredBy", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer3.getId(), offer3.getPosition(), offer3.getIsAccepted(), offer3.getOfferedBy(),
                                offer3.getCreatedAt(), offer3.getUpdatedAt())
                );
        assertThat(users.getData().get(1).getOffers())
                .extracting("offerId", "position", "isAccepted", "offeredBy", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getPosition(), offer2.getIsAccepted(), offer2.getOfferedBy(),
                                offer2.getCreatedAt(), offer2.getUpdatedAt())
                );

        assertEquals(pageSize, users.getData().size());
        assertEquals(3, users.getTotal());
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 프로필 페이징 조회시 예외가 발생한다.")
    void givenNonExistingUser_whenFindPageUser_thenThrow() {
        // given
        String username = "tester1";

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when & then
        assertThatThrownBy(() -> profileService.findPageUser(username, Position.NONE, pageFrom, pageSize))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    private ProfileDefaultRequest createValidProfileDefaultRequest() {
        EducationDefaultRequest educationRequest = createEducationDefaultRequest(null, "가보자잇대",
                LocalDate.of(2004, 1, 1), null, true);
        PortfolioDefaultRequest portfolioRequest = createPortfolioDefaultRequest(null, "포트폴리오",
                "github.com/gabojait", Media.LINK.toString());
        SkillDefaultRequest skillRequest = createSkillDefaultRequest(null, "스킬", true, Level.HIGH.toString());
        WorkDefaultRequest workRequest = createWorkDefaultRequest(null, "경력", "경력", LocalDate.of(2004, 1, 1),
                LocalDate.of(2005, 1, 1), false);

        return ProfileDefaultRequest.builder()
                .position(Position.DESIGNER.toString())
                .educations(List.of(educationRequest))
                .portfolios(List.of(portfolioRequest))
                .skills(List.of(skillRequest))
                .works(List.of(workRequest))
                .build();
    }

    private WorkDefaultRequest createWorkDefaultRequest(Long workId,
                                                        String corporationName,
                                                        String workDescription,
                                                        LocalDate startedAt,
                                                        LocalDate endedAt,
                                                        Boolean isCurrent) {
        return WorkDefaultRequest.builder()
                .workId(workId)
                .corporationName(corporationName)
                .workDescription(workDescription)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .isCurrent(isCurrent)
                .build();
    }

    private SkillDefaultRequest createSkillDefaultRequest(Long skillId,
                                                          String skillName,
                                                          Boolean isExperienced,
                                                          String level) {
        return SkillDefaultRequest.builder()
                .skillId(skillId)
                .skillName(skillName)
                .isExperienced(isExperienced)
                .level(level)
                .build();
    }

    private PortfolioDefaultRequest createPortfolioDefaultRequest(Long portfolioId,
                                                                  String portfolioName,
                                                                  String portfolioUrl,
                                                                  String media) {
        return PortfolioDefaultRequest.builder()
                .portfolioId(portfolioId)
                .portfolioName(portfolioName)
                .portfolioUrl(portfolioUrl)
                .media(media)
                .build();
    }

    private EducationDefaultRequest createEducationDefaultRequest(Long educationId,
                                                                  String institutionName,
                                                                  LocalDate startedAt,
                                                                  LocalDate endedAt,
                                                                  Boolean isCurrent) {
        return EducationDefaultRequest.builder()
                .educationId(educationId)
                .institutionName(institutionName)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .isCurrent(isCurrent)
                .build();
    }

    private Offer createOffer(User user, Team team) {
        return Offer.builder()
                .offeredBy(OfferedBy.LEADER)
                .position(Position.BACKEND)
                .user(user)
                .team(team)
                .build();
    }

    private Education createEducation(String institutionName, LocalDate startedAt, User user) {
        return Education.builder()
                .institutionName(institutionName)
                .startedAt(startedAt)
                .endedAt(LocalDate.of(2023,8, 1))
                .isCurrent(false)
                .user(user)
                .build();
    }

    private Portfolio createPortfolio(String portfolioName, User user) {
        return Portfolio.builder()
                .portfolioName(portfolioName)
                .portfolioUrl("github.com/gabojait")
                .media(Media.LINK)
                .user(user)
                .build();
    }

    private Skill createSkill(String skillName, User user) {
        return Skill.builder()
                .skillName(skillName)
                .level(Level.MID)
                .isExperienced(true)
                .user(user)
                .build();
    }

    private Work createWork(String corporationName, LocalDate startedAt, User user) {
        return Work.builder()
                .corporationName(corporationName)
                .workDescription("백엔드 개발")
                .startedAt(startedAt)
                .isCurrent(true)
                .user(user)
                .build();
    }

    private Team createTeam(String projectName) {
        return Team.builder()
                .projectName(projectName)
                .projectDescription("프로젝트 설명")
                .expectation("내용입니다.")
                .openChatUrl("kakao.com/o/project")
                .designerMaxCnt((byte) 2)
                .backendMaxCnt((byte) 2)
                .frontendMaxCnt((byte) 2)
                .managerMaxCnt((byte) 2)
                .build();
    }

    private TeamMember createTeamMember(User user, Team team) {
        return TeamMember.builder()
                .position(Position.BACKEND)
                .isLeader(true)
                .user(user)
                .team(team)
                .build();
    }

    private Review createReview(TeamMember reviewer, TeamMember reviewee) {
        return Review.builder()
                .rating((byte) 3)
                .post("리뷰입니다.")
                .reviewer(reviewer)
                .reviewee(reviewee)
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
        userRepository.save(user);

        return user;
    }
}