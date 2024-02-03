package com.gabojait.gabojaitspring.api.service.profile;

import com.gabojait.gabojaitspring.api.dto.profile.request.*;
import com.gabojait.gabojaitspring.api.dto.profile.response.*;
import com.gabojait.gabojaitspring.api.vo.profile.ProfileVO;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.common.util.FileUtility;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.profile.*;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
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
     * @param userId 회원 식별자
     * @return 프로필 본인 조회 응답
     */
    public ProfileFindMyselfResponse findMyProfile(long userId) {
        User user = findUser(userId);
        List<Skill> skills = skillRepository.findAll(user.getId());
        ProfileVO profile = findProfileInfo(user);

        return new ProfileFindMyselfResponse(user, skills, profile);
    }

    /**
     * 다른 프로필 조회 |
     * 404(USER_NOT_FOUND)
     * @param myUserId 내 회원 아이디
     * @param otherUserId 다른 회원 식별자
     * @return 프로필 단건 조회 응답
     */
    @Transactional
    public ProfileFindOtherResponse findOtherProfile(long myUserId, long otherUserId) {
        User otherUser = findUser(otherUserId);

        List<Skill> skills = skillRepository.findAll(otherUserId);
        ProfileVO profile = findProfileInfo(otherUser);
        List<Offer> offers = new ArrayList<>();
        Boolean isFavorite = null;

        if (myUserId != otherUserId) {
            offers = offerRepository.findAllByUserId(otherUserId, myUserId);
            isFavorite = favoriteRepository.existsUser(myUserId, otherUserId);

            otherUser.visit();
        }

        return new ProfileFindOtherResponse(otherUser, skills, profile, offers, isFavorite);
    }

    /**
     * 프로필 이미지 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * 415(IMAGE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     * @param userId 회원 식별자
     * @param image 프로필 이미지
     * @return 프로필 이미지 응답
     */
    @Transactional
    public ProfileImageResponse uploadProfileImage(long userId, MultipartFile image) {
        User user = findUser(userId);

        String url = fileUtility.upload(profileImgBucketName, user.getId().toString(), UUID.randomUUID().toString(),
                image, true);

        user.updateImageUrl(url);

        List<Skill> skills = skillRepository.findAll(user.getId());
        ProfileVO profile = findProfileInfo(user);

        return new ProfileImageResponse(user, skills, profile);
    }

    /**
     * 프로필 이미지 삭제 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @return 프로필 이미지 응답
     */
    @Transactional
    public ProfileImageResponse deleteProfileImage(long userId) {
        User user = findUser(userId);

        user.updateImageUrl(null);

        List<Skill> skills = skillRepository.findAll(user.getId());
        ProfileVO profile = findProfileInfo(user);

        return new ProfileImageResponse(user, skills, profile);
    }

    /**
     * 팀 찾기 여부 업데이트 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param isSeekingTeam 팀 찾기 여부
     */
    @Transactional
    public void updateIsSeekingTeam(long userId, boolean isSeekingTeam) {
        User user = findUser(userId);

        user.updateIsSeekingTeam(isSeekingTeam);
    }

    /**
     * 프로필 업데이트 |
     * 400(EDUCATION_DATE_INVALID / EDUCATION_ENDED_AT_FIELD_REQUIRED / WORK_DATE_INVALID / WORK_ENDED_AT_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param request 프로필 업데이트 요청
     * @return 프로필 업데이트 응답
     */
    @Transactional
    public ProfileUpdateResponse updateProfile(long userId, ProfileUpdateRequest request) {
        User user = findUser(userId);

        validateDate(request.getEducations(), request.getWorks());

        user.updatePosition(Position.valueOf(request.getPosition()));
        updateEducations(user, request.getEducations());
        updatePortfolios(user, request.getPortfolios());
        updateSkills(user, request.getSkills());
        updateWorks(user, request.getWorks());

        List<Skill> skills = skillRepository.findAll(user.getId());
        ProfileVO profile = findProfileInfo(user);

        return new ProfileUpdateResponse(user, skills, profile);
    }

    /**
     * 학력들 생성 수정 및 삭제 |
     * @param user 회원
     * @param requests 학력 업데이트 요청들
     */
    @Transactional
    public void updateEducations(User user, List<EducationUpdateRequest> requests) {
        List<Education> currentEducations = educationRepository.findAll(user.getId());

        if (requests.isEmpty()) {
            educationRepository.deleteAll(currentEducations);
            return;
        }

        for (EducationUpdateRequest request : requests) {
            if (request.getEducationId() == null)
                educationRepository.save(request.toEntity(user));
            else
                currentEducations.stream()
                        .filter(currentEducation -> currentEducation.getId().equals(request.getEducationId()))
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
     * @param requests 포트폴리오 업데이트 요청들
     */
    @Transactional
    public void updatePortfolios(User user, List<PortfolioUpdateRequest> requests) {
        List<Portfolio> currentPortfolios = portfolioRepository.findAll(user.getId());

        if (requests.isEmpty()) {
            portfolioRepository.deleteAll(currentPortfolios);
            return;
        }

        for (PortfolioUpdateRequest request : requests) {
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
     * @param requests 기술 업데이트 요청들
     */
    @Transactional
    public void updateSkills(User user, List<SkillUpdateRequest> requests) {
        List<Skill> currentSkills = skillRepository.findAll(user.getId());

        if (requests.isEmpty()) {
            skillRepository.deleteAll(currentSkills);
            return;
        }

        for (SkillUpdateRequest request : requests) {
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
     * @param requests 경력 업데이트 요청들
     */
    @Transactional
    public void updateWorks(User user, List<WorkUpdateRequest> requests) {
        List<Work> currentWorks = workRepository.findAll(user.getId());

        if (requests.isEmpty()) {
            workRepository.deleteAll(currentWorks);
            return;
        }

        for (WorkUpdateRequest request : requests) {
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
     * @param educationRequests 학력 업데이트 요청들
     * @param workRequests 경력 업데이트 요청들
     */
    private void validateDate(List<EducationUpdateRequest> educationRequests, List<WorkUpdateRequest> workRequests) {
        educationRequests.forEach(education -> {
            if (education.getEndedAt() == null) {
                if (!education.getIsCurrent())
                    throw new CustomException(EDUCATION_ENDED_AT_FIELD_REQUIRED);
                } else {
                    if (education.getStartedAt().isAfter(education.getEndedAt()))
                        throw new CustomException(EDUCATION_DATE_INVALID);
                }
            });

        workRequests.forEach(work -> {
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
     * @param userId 회원 식별자
     * @param profileDescription 자기 소개
     */
    @Transactional
    public void updateProfileDescription(long userId, String profileDescription) {
        User user = findUser(userId);

        user.updateProfileDescription(profileDescription);
    }

    /**
     * 포트폴리오 파일 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * 415(FILE_TYPE_UNSUPPORTED)
     * @param userId 회원 식별자
     * @param file 포트폴리오 파일
     * @return 포트폴리오 URL 응답
     */
    public PortfolioUrlResponse uploadPortfolioFile(long userId, MultipartFile file) {
        User user = findUser(userId);

        String portfolioUrl = fileUtility.upload(portfolioBucketName, user.getId().toString(),
                UUID.randomUUID().toString(), file, false);

        return new PortfolioUrlResponse(portfolioUrl);
    }

    /**
     * 프로필 페이징 조회 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param position 포지션
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 프로필 제안 응답들
     */
    public PageData<List<ProfilePageResponse>> findPageUser(long userId,
                                                            Position position,
                                                            long pageFrom,
                                                            int pageSize) {
        User user = findUser(userId);

        PageData<List<User>> users = userRepository.findPage(position, pageFrom, pageSize);
        List<Skill> skills = skillRepository.findAllInFetchUser(users.getData()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList()));
        List<Offer> offers = offerRepository.findAllInUserIds(users.getData()
                        .stream()
                        .map(User::getId)
                        .collect(Collectors.toList()),
                user.getId());

        Map<Long, List<Skill>> sMap = skills.stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));
        Map<Long, List<Offer>> oMap = offers.stream()
                .collect(Collectors.groupingBy(o -> o.getUser().getId()));

        List<ProfilePageResponse> responses = users.getData()
                .stream()
                .map(u ->
                        new ProfilePageResponse(u,
                                sMap.getOrDefault(u.getId(), Collections.emptyList()),
                                oMap.getOrDefault(u.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        return new PageData<>(responses, users.getTotal());
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
     * 프로필 정보 조회 |
     * @param user 회원
     * @return 프로필
     */
    private ProfileVO findProfileInfo(User user) {
        List<Education> educations = educationRepository.findAll(user.getId());
        List<Portfolio> portfolios = portfolioRepository.findAll(user.getId());
        List<Work> works = workRepository.findAll(user.getId());
        List<TeamMember> teamMembers = teamMemberRepository.findAllFetchTeam(user.getId());
        PageData<List<Review>> reviews = reviewRepository.findPage(user.getId(), Long.MAX_VALUE, 3);
        long reviewCnt = reviewRepository.countPrevious(user.getId(), Long.MAX_VALUE);

        return new ProfileVO(educations, portfolios, works, teamMembers, reviews, reviewCnt);
    }
}
