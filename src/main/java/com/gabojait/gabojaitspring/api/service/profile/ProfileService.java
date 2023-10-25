package com.gabojait.gabojaitspring.api.service.profile;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.profile.request.*;
import com.gabojait.gabojaitspring.api.dto.profile.response.PortfolioUrlResponse;
import com.gabojait.gabojaitspring.api.dto.profile.response.ProfileDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.profile.response.ProfileDetailResponse;
import com.gabojait.gabojaitspring.api.dto.profile.response.ProfileOfferResponse;
import com.gabojait.gabojaitspring.api.vo.profile.ProfileVO;
import com.gabojait.gabojaitspring.common.util.FileUtility;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.profile.*;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.favorite.FavoriteRepository;
import com.gabojait.gabojaitspring.repository.offer.OfferRepository;
import com.gabojait.gabojaitspring.repository.profile.EducationRepository;
import com.gabojait.gabojaitspring.repository.profile.PortfolioRepository;
import com.gabojait.gabojaitspring.repository.profile.SkillRepository;
import com.gabojait.gabojaitspring.repository.profile.WorkRepository;
import com.gabojait.gabojaitspring.repository.review.ReviewRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    @Value(value = "${s3.bucket.profile-img}")
    private String profileImgBucketName;

    @Value(value = "${s3.bucket.portfolio-file}")
    private String portfolioBucketName;

    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillRepository skillRepository;
    private final WorkRepository workRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final OfferRepository offerRepository;
    private final FavoriteRepository favoriteRepository;
    private final FileUtility fileUtility;

    /**
     * 내 프로필 조회 |
     * 404(USER_NOT_FOUND)
     * @param username 회원 아이디
     * @return 프로필 기본 응답
     */
    public ProfileDefaultResponse findMyProfile(String username) {
        User user = findUser(username);
        List<Skill> skills = skillRepository.findAll(user.getId());
        ProfileVO profile = findProfileInfo(user);

        return new ProfileDefaultResponse(user, skills, profile);
    }

    /**
     * 다른 프로필 조회 |
     * 404(USER_NOT_FOUND)
     * @param username 내 회원 아이디
     * @param userId 타겟 회원 식별자
     * @return 프로필 상세 응답
     */
    @Transactional
    public ProfileDetailResponse findOtherProfile(String username, long userId) {
        User targetUser = findUser(userId);

        List<Skill> skills = skillRepository.findAll(targetUser.getId());
        ProfileVO profile = findProfileInfo(targetUser);
        List<Offer> offers = new ArrayList<>();
        Boolean isFavorite = null;

        if (!username.equals(targetUser.getUsername())) {
            User user = findUser(username);

            offers = offerRepository.findAllByUserId(targetUser.getId(), user.getId());
            isFavorite = favoriteRepository.existsUser(user.getId(), targetUser.getId());

            targetUser.visit();
        }

        return new ProfileDetailResponse(targetUser, skills, profile, offers, isFavorite);
    }

    /**
     * 프로필 이미지 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * 415(IMAGE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     * @param username 회원 아이디
     * @param image 프로필 이미지
     * @return 프로필 기본 응답
     */
    @Transactional
    public ProfileDefaultResponse uploadProfileImage(String username, MultipartFile image) {
        User user = findUser(username);

        String url = fileUtility.upload(profileImgBucketName, user.getId().toString(), UUID.randomUUID().toString(),
                image, true);

        user.updateImageUrl(url);

        List<Skill> skills = skillRepository.findAll(user.getId());
        ProfileVO profile = findProfileInfo(user);

        return new ProfileDefaultResponse(user, skills, profile);
    }

    /**
     * 프로필 이미지 삭제 |
     * 404(USER_NOT_FOUND)
     * @param username 회원 아이디
     * @return 프로필 기본 응답
     */
    @Transactional
    public ProfileDefaultResponse deleteProfileImage(String username) {
        User user = findUser(username);

        user.updateImageUrl(null);

        List<Skill> skills = skillRepository.findAll(user.getId());
        ProfileVO profile = findProfileInfo(user);

        return new ProfileDefaultResponse(user, skills, profile);
    }

    /**
     * 팀 찾기 여부 업데이트 |
     * 404(USER_NOT_FOUND)
     * @param username 회원 아이디
     * @param isSeekingTeam 팀 찾기 여부
     */
    @Transactional
    public void updateIsSeekingTeam(String username, boolean isSeekingTeam) {
        User user = findUser(username);

        user.updateIsSeekingTeam(isSeekingTeam);
    }

    /**
     * 프로필 업데이트 |
     * 400(EDUCATION_DATE_INVALID / EDUCATION_ENDED_AT_FIELD_REQUIRED / WORK_DATE_INVALID / WORK_ENDED_AT_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * @param username 회원 아이디
     * @param request 프로필 기본 요청
     * @return 프로필 기본 응답
     */
    @Transactional
    public ProfileDefaultResponse updateProfile(String username, ProfileDefaultRequest request) {
        User user = findUser(username);

        validateDate(request.getEducations(), request.getWorks());

        user.updatePosition(Position.valueOf(request.getPosition()));
        updateEducations(user, request.getEducations());
        updatePortfolios(user, request.getPortfolios());
        updateSkills(user, request.getSkills());
        updateWorks(user, request.getWorks());

        List<Skill> skills = skillRepository.findAll(user.getId());
        ProfileVO profile = findProfileInfo(user);

        return new ProfileDefaultResponse(user, skills, profile);
    }

    /**
     * 학력들 생성 수정 및 삭제 |
     * @param user 회원
     * @param requests 학력 기본 요청
     */
    @Transactional
    public void updateEducations(User user, List<EducationDefaultRequest> requests) {
        List<Education> currentEducations = educationRepository.findAll(user.getId());

        if (requests.isEmpty()) {
            educationRepository.deleteAll(currentEducations);
            return;
        }

        for (EducationDefaultRequest request : requests) {
            if (request.getEducationId() == null)
                educationRepository.save(request.toEntity(user));
            else
                currentEducations.stream()
                        .filter(currentEducation -> request.getEducationId().equals(currentEducation.getId()))
                        .findFirst()
                        .ifPresent(currentEducation -> {
                            currentEducations.remove(currentEducation);

                            if (request.hashCode(user) != currentEducation.hashCode())
                                currentEducation.update(request.getInstitutionName(),
                                        request.getStartedAt(),
                                        request.getEndedAt(),
                                        request.getIsCurrent());
                        });
        }

        educationRepository.deleteAll(currentEducations);
    }

    /**
     * 포트폴리오들 생성 수정 및 삭제
     * @param user 회원
     * @param requests 포트폴리오 기본 요청
     */
    @Transactional
    public void updatePortfolios(User user, List<PortfolioDefaultRequest> requests) {
        List<Portfolio> currentPortfolios = portfolioRepository.findAll(user.getId());

        if (requests.isEmpty()) {
            portfolioRepository.deleteAll(currentPortfolios);
            return;
        }

        for (PortfolioDefaultRequest request : requests) {
            if (request.getPortfolioId() == null)
                portfolioRepository.save(request.toEntity(user));
            else
                currentPortfolios.stream()
                        .filter(currentPortfolio -> request.getPortfolioId().equals(currentPortfolio.getId()))
                        .findFirst()
                        .ifPresent(currentPortfolio -> {
                            currentPortfolios.remove(currentPortfolio);

                            if (request.hashCode(user) != currentPortfolio.hashCode())
                                currentPortfolio.update(request.getPortfolioName(),
                                        request.getPortfolioUrl(),
                                        Media.valueOf(request.getMedia()));
                        });
        }

        portfolioRepository.deleteAll(currentPortfolios);
    }

    /**
     * 기술들 생성 수정 및 삭제
     * @param user 회원
     * @param requests 기술 기본 요청
     */
    @Transactional
    public void updateSkills(User user, List<SkillDefaultRequest> requests) {
        List<Skill> currentSkills = skillRepository.findAll(user.getId());

        if (requests.isEmpty()) {
            skillRepository.deleteAll(currentSkills);
            return;
        }

        for (SkillDefaultRequest request : requests) {
            if (request.getSkillId() == null)
                skillRepository.save(request.toEntity(user));
            else
                currentSkills.stream()
                        .filter(currentSkill -> request.getSkillId().equals(currentSkill.getId()))
                        .findFirst()
                        .ifPresent(currentSkill -> {
                            currentSkills.remove(currentSkill);

                            if (request.hashCode(user) != currentSkill.hashCode())
                                currentSkill.update(request.getSkillName(),
                                        Level.valueOf(request.getLevel()),
                                        request.getIsExperienced());
                        });
        }

        skillRepository.deleteAll(currentSkills);
    }

    /**
     * 경력들 생성 수정 및 삭제
     * @param user 회원
     * @param requests 경력 기본 요청
     */
    @Transactional
    public void updateWorks(User user, List<WorkDefaultRequest> requests) {
        List<Work> currentWorks = workRepository.findAll(user.getId());

        if (requests.isEmpty()) {
            workRepository.deleteAll(currentWorks);
            return;
        }

        for (WorkDefaultRequest request : requests) {
            if (request.getWorkId() == null)
                workRepository.save(request.toEntity(user));
            else
                currentWorks.stream()
                        .filter(currentWork -> request.getWorkId().equals(currentWork.getId()))
                        .findFirst()
                        .ifPresent(currentWork -> {
                            currentWorks.remove(currentWork);

                            if (request.hashCode(user) != currentWork.hashCode())
                                currentWork.update(request.getCorporationName(),
                                        request.getWorkDescription(),
                                        request.getStartedAt(),
                                        request.getEndedAt(),
                                        request.getIsCurrent());
                        });
        }

        workRepository.deleteAll(currentWorks);
    }

    /**
     * 날짜 검증 |
     * 400(EDUCATION_DATE_INVALID / EDUCATION_ENDED_AT_FIELD_REQUIRED / WORK_DATE_INVALID /
     * WORK_ENDED_AT_FIELD_REQUIRED)
     * @param educations 학력들
     * @param works 경력들
     */
    private void validateDate(List<EducationDefaultRequest> educations, List<WorkDefaultRequest> works) {
        educations.forEach(education -> {
            if (education.getEndedAt() == null) {
                if (!education.getIsCurrent())
                    throw new CustomException(EDUCATION_ENDED_AT_FIELD_REQUIRED);
                } else {
                    if (education.getStartedAt().isAfter(education.getEndedAt()))
                        throw new CustomException(EDUCATION_DATE_INVALID);
                }
            });

        works.forEach(work -> {
                if (work.getEndedAt() == null) {
                    if (!work.getIsCurrent())
                        throw new CustomException(WORK_ENDED_AT_FIELD_REQUIRED);
                } else {
                    if (work.getStartedAt().isAfter(work.getEndedAt()))
                        throw new CustomException(WORK_DATE_INVALID);
                }
            });
    }

    /**
     * 자기소개 업데이트 |
     * 404(USER_NOT_FOUND)
     * @param username 회원 아이디
     * @param profileDescription 자기 소개
     */
    @Transactional
    public void updateProfileDescription(String username, String profileDescription) {
        User user = findUser(username);

        user.updateProfileDescription(profileDescription);
    }

    /**
     * 포트폴리오 파일 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * 415(FILE_TYPE_UNSUPPORTED)
     * @param username 회원 아이디
     * @param file 포트폴리오 파일
     * @return 포트폴리오 URL 응답
     */
    public PortfolioUrlResponse uploadPortfolioFile(String username, MultipartFile file) {
        User user = findUser(username);

        String portfolioUrl = fileUtility.upload(portfolioBucketName, user.getId().toString(),
                UUID.randomUUID().toString(), file, false);

        return new PortfolioUrlResponse(portfolioUrl);
    }

    /**
     * 프로필 페이징 조회 |
     * 404(USER_NOT_FOUND)
     * @param username 회원 아이디
     * @param position 포지션
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 프로필 제안 응답들
     */
    public PageData<List<ProfileOfferResponse>> findPageUser(String username, Position position, long pageFrom, int pageSize) {
        User user = findUser(username);

        Page<User> users = userRepository.findPage(position, pageFrom, pageSize);
        List<Skill> skills = skillRepository.findAllInFetchUser(users.stream()
                .map(User::getId)
                .collect(Collectors.toList()));
        List<Offer> offers = offerRepository.findAllInUserIds(users.stream()
                        .map(User::getId)
                        .collect(Collectors.toList()),
                user.getId());

        Map<Long, List<Skill>> sMap = skills.stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));
        Map<Long, List<Offer>> oMap = offers.stream()
                .collect(Collectors.groupingBy(o -> o.getUser().getId()));

        List<ProfileOfferResponse> responses = users.stream()
                .map(u ->
                        new ProfileOfferResponse(u,
                                sMap.getOrDefault(u.getId(), Collections.emptyList()),
                                oMap.getOrDefault(u.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        return new PageData<>(responses, users.getTotalElements());
    }

    /**
     * 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 아이디
     * @return 회원
     */
    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     * @param username 회원 아이디
     * @return 회원
     */
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 프로필 정보 조회 |
     * @param user 회원
     * @return 프로필
     */
    private ProfileVO findProfileInfo(User user) {
        List<Education> educations = educationRepository.findAll(user.getId());
        List<Portfolio> portfolios = portfolioRepository.findAll(user.getId());
        List<Work> works = workRepository.findAll(user.getId());
        List<TeamMember> teamMembers = teamMemberRepository.findAllFetchTeam(user.getId());
        Page<Review> reviews = reviewRepository.findPage(user.getId(), Long.MAX_VALUE, 3);
        long reviewCnt = reviewRepository.countPrevious(user.getId(), Long.MAX_VALUE);

        return new ProfileVO(educations, portfolios, works, teamMembers, reviews, reviewCnt);
    }
}
