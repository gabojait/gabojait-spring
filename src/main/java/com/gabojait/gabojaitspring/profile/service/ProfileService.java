package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.common.util.FileProvider;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.favorite.repository.FavoriteUserRepository;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.ProfileOrder;
import com.gabojait.gabojaitspring.profile.dto.ProfileSeekPageDto;
import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileOfferAndFavoriteResDto;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileSeekResDto;
import com.gabojait.gabojaitspring.profile.repository.EducationRepository;
import com.gabojait.gabojaitspring.profile.repository.PortfolioRepository;
import com.gabojait.gabojaitspring.profile.repository.SkillRepository;
import com.gabojait.gabojaitspring.profile.repository.WorkRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService {

    @Value(value = "${s3.bucket.profile-img}")
    private String profileImgBucketName;

    @Value(value = "${s3.bucket.portfolio-file}")
    private String portfolioBucketName;

    private final EducationRepository educationRepository;
    private final WorkRepository workRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final FavoriteUserRepository favoriteUserRepository;
    private final FileProvider fileProvider;
    private final GeneralProvider generalProvider;

    /**
     * 프로필 업데이트 |
     * 400(EDUCATION_DATE_INVALID / EDUCATION_ENDED_AT_FIELD_REQUIRED / WORK_DATE_INVALID /
     * WORK_ENDED_AT_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public User updateProfile(long userId, ProfileDefaultReqDto request) {
        User user = findOneUser(userId);

        validateDate(request.getEducations(), request.getWorks());

        updatePosition(user, Position.fromString(request.getPosition()));
        updateSkills(user, request.getSkills());
        updateEducations(user, request.getEducations());
        updateWorks(user, request.getWorks());
        updatePortfolios(user, request.getPortfolios());

        return user;
    }

    /**
     * 포트폴리오 파일 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 415(FILE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public String uploadPortfolioFile(long userId, MultipartFile multipartFile) {
        return fileProvider.upload(portfolioBucketName,
                String.valueOf(userId),
                UUID.randomUUID().toString(),
                multipartFile,
                false);
    }

    /**
     * 자기소개 업데이트 |
     * 404(USER_NOT_FOUND)
     */
    public void updateProfileDescription(long userId, String profileDescription) {
        User user = findOneUser(userId);
        user.updateProfileDescription(profileDescription);
    }

    /**
     * 팀 찾기 여부 업데이트 |
     * 404(USER_NOT_FOUND)
     */
    public void updateIsSeekingTeam(long userId, boolean isSeekingTeam) {
        User user = findOneUser(userId);
        user.updateIsSeekingTeam(isSeekingTeam);
    }

    /**
     * 프로필 이미지 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * 415(IMAGE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public User uploadProfileImage(long userId, MultipartFile multipartFile) {
        User user = findOneUser(userId);

        String url = fileProvider.upload(profileImgBucketName,
                user.getId().toString(),
                UUID.randomUUID().toString(),
                multipartFile,
                true);

        user.updateImageUrl(url);

        return user;
    }

    /**
     * 프로필 이미지 삭제 |
     * 404(USER_NOT_FOUND)
     */
    public User deleteProfileImage(long userId) {
        User user = findOneUser(userId);
        user.updateImageUrl(null);
        return user;
    }

    /**
     * 회원 식별자로 회원으로 타 회원 프로필 단건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public ProfileOfferAndFavoriteResDto findOneOtherProfile(long userId, Long otherUserId) {
        User user = findOneUser(userId);

        if (userId == otherUserId)
            return new ProfileOfferAndFavoriteResDto(user, List.of(), null);

        User otherUser = findOneUser(otherUserId);

        otherUser.incrementVisitedCnt();

        List<Offer> offers = new ArrayList<>();
        Boolean isFavorite = null;

        if (user.isLeader()) {
            offers = findAllOffersToUser(user, otherUser);
            isFavorite = isFavoriteUser(otherUser, user);
        }

        return new ProfileOfferAndFavoriteResDto(otherUser, offers, isFavorite);
    }

    /**
     * 기술 저장 |
     * 500(SERVER_ERROR)
     */
    public Skill saveSkill(Skill skill) {
        try {
            return skillRepository.save(skill);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 학력 저장 |
     * 500(SERVER_ERROR)
     */
    public Education saveEducation(Education education) {
        try {
            return educationRepository.save(education);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 경력 저장 |
     * 500(SERVER_ERROR)
     */
    public Work saveWork(Work work) {
        try {
            return workRepository.save(work);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 저장 |
     * 500(SERVER_ERROR)
     */
    public Portfolio savePortfolio(Portfolio portfolio) {
        try {
            return portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 기술 소프트 삭제
     */
    private void softDeleteSkill(Skill skill) {
        skill.delete();
    }

    /**
     * 학력 소프트 삭제
     */
    private void softDeleteEducation(Education education) {
        education.delete();
    }

    /**
     * 경력 소프트 삭제
     */
    private void softDeleteWork(Work work) {
        work.delete();
    }

    /**
     * 포트폴리오 소프트 삭제
     */
    private void softDeletePortfolio(Portfolio portfolio) {
        portfolio.delete();
    }

    /**
     * 포지션과 프로필 정렬 기준으로 회원 페이징 다건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public ProfileSeekPageDto findManyUsersByPositionWithProfileOrder(long userId,
                                                                      String position,
                                                                      String profileOrder,
                                                                      Integer pageFrom,
                                                                      Integer pageSize) {
        User user = findOneUser(userId);
        Position p = Position.fromString(position);
        ProfileOrder po = ProfileOrder.fromString(profileOrder);
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);

        Page<User> users;

        if (p.equals(Position.NONE)) {
            switch (po.name().toLowerCase()) {
                case "rating":
                    users = findManyUsersByRating(pageable);
                    break;
                case "popularity":
                    users = findManyUsersByPopularity(pageable);
                    break;
                default:
                    users = findManyUsersByActive(pageable);
                    break;
            }
        } else {
            switch (po.name().toLowerCase()) {
                case "rating":
                    users = findManyUsersPositionByRating(p, pageable);
                    break;
                case "popularity":
                    users = findManyUsersPositionByPopularity(p, pageable);
                    break;
                default:
                    users = findManyUsersPositionByActive(p, pageable);
                    break;
            }
        }

        List<ProfileSeekResDto> profileSeekResDtos = new ArrayList<>();

        if (user.isLeader()) {
            for (User u : users) {
                List<Offer> offers = findAllOffersToUser(user, u);
                profileSeekResDtos.add(new ProfileSeekResDto(u, offers));
            }
        } else {
            for (User u : users)
                profileSeekResDtos.add(new ProfileSeekResDto(u, List.of()));
        }

        return new ProfileSeekPageDto(profileSeekResDtos, users.getTotalPages());
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    public User findOneUser(long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 전체 포지션을 평점순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersByRating(Pageable pageable) {
        try {
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 인기순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersByPopularity(Pageable pageable) {
        try {
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 활동순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersByActive(Pageable pageable) {
        try {
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestAtDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 평점순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersPositionByRating(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(
                    position.getType(),
                    pageable
            );
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 인기순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersPositionByPopularity(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                    position.getType(),
                    pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 활동순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersPositionByActive(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestAtDesc(
                    position.getType(),
                    pageable
            );
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 리더와 회원으로 리더가 특정 회원에게 보낸 전체 제안 조회 |
     * 500(SERVER_ERROR)
     */
    private List<Offer> findAllOffersToUser(User leader, User user) {
        Team team = leader.getTeamMembers().get(leader.getTeamMembers().size() - 1).getTeam();

        try {
            return offerRepository.findAllByUserAndTeamAndIsAcceptedIsNullAndIsDeletedIsFalse(user, team);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원으로 기술 전체 조회 |
     * 500(SERVER_ERROR)
     */
    private List<Skill> findAllSkill(User user) {
        try {
            return skillRepository.findAllByUserAndIsDeletedIsFalse(user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원으로 학력 전체 조회 |
     * 500(SERVER_ERROR)
     */
    private List<Education> findAllEducation(User user) {
        try {
            return educationRepository.findAllByUserAndIsDeletedIsFalse(user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원으로 경력 전체 조회 |
     * 500(SERVER_ERROR)
     */
    private List<Work> findAllWork(User user) {
        try {
            return workRepository.findAllByUserAndIsDeletedIsFalse(user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원으로 포트폴리오 전체 조회 |
     * 500(SERVER_ERROR)
     */
    private List<Portfolio> findAllPortfolio(User user) {
        try {
            return portfolioRepository.findAllByUserAndIsDeletedIsFalse(user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 찜한 회원 여부 확인 |
     * 500(SERVER_ERROR)
     */
    private boolean isFavoriteUser(User user, User leader) {
        Team team = leader.getTeamMembers().get(leader.getTeamMembers().size() - 1).getTeam();

        try {
            Optional<FavoriteUser> favoriteUser =
                    favoriteUserRepository.findByTeamAndUserAndIsDeletedIsFalse(team, user);

            return favoriteUser.isPresent();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 포지션 업데이트
     */
    private void updatePosition(User user, Position position) {
        user.updatePosition(position);
    }

    /**
     * 기술들 업데이트 |
     * 500(SERVER_ERROR)
     */
    private void updateSkills(User user, List<SkillDefaultReqDto> requests) {
        List<Skill> currentSkills = findAllSkill(user);

        if (requests.isEmpty()) {
            deleteSkills(currentSkills);
            return;
        }

        List<SkillDefaultReqDto> newSkills = new ArrayList<>();

        for (SkillDefaultReqDto request : requests) {
            boolean isNewSkill = true;

            for (Skill skill : currentSkills)
                if (request.getSkillName().trim().equals(skill.getSkillName())
                        && request.getIsExperienced().equals(skill.getIsExperienced())
                        && Level.fromString(request.getLevel()).equals(Level.fromChar(skill.getLevel()))) {
                    currentSkills.remove(skill);
                    isNewSkill = false;
                    break;
                }

            if (isNewSkill)
                newSkills.add(request);
        }

        createSkills(user, newSkills);
        deleteSkills(currentSkills);
    }

    /**
     * 기술들 생성 |
     * 500(SERVER_ERROR)
     */
    private void createSkills(User user, List<SkillDefaultReqDto> skills) {
        for (SkillDefaultReqDto skill : skills) {
            Skill s = skill.toEntity(user);
            saveSkill(s);
        }
    }

    /**
     * 기술들 삭제
     */
    private void deleteSkills(List<Skill> skills) {
        for (Skill skill : skills) {
            softDeleteSkill(skill);
        }
    }

    /**
     * 학력들 업데이트 |
     * 500(SERVER_ERROR)
     */
    private void updateEducations(User user, List<EducationDefaultReqDto> requests) {
        List<Education> currentEducations = findAllEducation(user);

        if (requests.isEmpty()) {
            deleteEducations(currentEducations);
            return;
        }

        List<EducationDefaultReqDto> newEducations = new ArrayList<>();

        for (EducationDefaultReqDto request : requests) {
            boolean isNewEducation = true;

            for (Education education : currentEducations)
                if (request.getInstitutionName().trim().equals(education.getInstitutionName())
                        && request.getStartedAt().equals(education.getStartedAt())
                        && request.getEndedAt().equals(education.getEndedAt())
                        && request.getIsCurrent().equals(education.getIsCurrent())) {
                    currentEducations.remove(education);
                    isNewEducation = false;
                    break;
                }

            if (isNewEducation)
                newEducations.add(request);
        }

        createEducations(user, newEducations);
        deleteEducations(currentEducations);
    }

    /**
     * 학력들 생성 |
     * 500(SERVER_ERROR)
     */
    private void createEducations(User user, List<EducationDefaultReqDto> educations) {
        for (EducationDefaultReqDto education : educations) {
            Education e = education.toEntity(user);
            saveEducation(e);
        }
    }

    /**
     * 학력들 삭제
     */
    private void deleteEducations(List<Education> educations) {
        for (Education education : educations)
            softDeleteEducation(education);
    }

    /**
     * 경력들 업데이트 |
     * 500(SERVER_ERROR)
     */
    private void updateWorks(User user, List<WorkDefaultReqDto> requests) {
        List<Work> currentWorks = findAllWork(user);

        if (requests.isEmpty()) {
            deleteWorks(currentWorks);
            return;
        }

        List<WorkDefaultReqDto> newWorks = new ArrayList<>();

        for (WorkDefaultReqDto request : requests) {
            boolean isNewWork = true;

            for (Work work : currentWorks)
                if (request.getCorporationName().trim().equals(work.getCorporationName())
                        && request.getWorkDescription().trim().equals(work.getWorkDescription())
                        && request.getStartedAt().equals(work.getStartedAt())
                        && request.getEndedAt().equals(work.getEndedAt())
                        && request.getIsCurrent().equals(work.getIsCurrent())) {
                    currentWorks.remove(work);
                    isNewWork = false;
                    break;
                }

            if (isNewWork)
                newWorks.add(request);
        }

        createWorks(user, newWorks);
        deleteWorks(currentWorks);
    }

    /**
     * 경력들 생성 |
     * 500(SERVER_ERROR)
     */
    private void createWorks(User user, List<WorkDefaultReqDto> works) {
        for (WorkDefaultReqDto work : works) {
            Work w = work.toEntity(user);
            saveWork(w);
        }
    }

    /**
     * 경력들 삭제
     */
    private void deleteWorks(List<Work> works) {
        for (Work work : works)
            softDeleteWork(work);
    }

    /**
     * 포트폴리오 업데이트 |
     * 500(SERVER_ERROR)
     */
    private void updatePortfolios(User user, List<PortfolioDefaultReqDto> requests) {
        List<Portfolio> currentPortfolios = findAllPortfolio(user);

        if (requests.isEmpty())
            deletePortfolios(currentPortfolios);

        List<PortfolioDefaultReqDto> newPortfolios = new ArrayList<>();

        for (PortfolioDefaultReqDto request : requests) {
            boolean isNewPortfolio = true;

            for (Portfolio portfolio : currentPortfolios)
                if (request.getPortfolioName().trim().equals(portfolio.getPortfolioName())
                        && request.getPortfolioUrl().trim().equals(portfolio.getPortfolioUrl())
                        && Media.fromString(request.getMedia()).equals(Media.fromChar(portfolio.getMedia()))) {
                    currentPortfolios.remove(portfolio);
                    isNewPortfolio = false;
                    break;
                }

            if (isNewPortfolio)
                newPortfolios.add(request);
        }

        createPortfolios(user, newPortfolios);
        deletePortfolios(currentPortfolios);
    }

    /**
     * 포트폴리오 생성 |
     * 500(SERVER_ERROR)
     */
    private void createPortfolios(User user, List<PortfolioDefaultReqDto> portfolios) {
        for (PortfolioDefaultReqDto portfolio : portfolios) {
            Portfolio p = portfolio.toEntity(user);
            savePortfolio(p);
        }
    }

    /**
     * 포트폴리오들 삭제
     */
    private void deletePortfolios(List<Portfolio> portfolios) {
        for (Portfolio portfolio : portfolios)
            softDeletePortfolio(portfolio);
    }

    /**
     * 날짜 검증 |
     * 400(EDUCATION_DATE_INVALID / EDUCATION_ENDED_AT_FIELD_REQUIRED / WORK_DATE_INVALID /
     * WORK_ENDED_AT_FIELD_REQUIRED)
     */
    private void validateDate(List<EducationDefaultReqDto> educations, List<WorkDefaultReqDto> works) {
        for (EducationDefaultReqDto education : educations) {
            if (education.getIsCurrent())
                if (education.getStartedAt().isAfter(education.getEndedAt()))
                    throw new CustomException(EDUCATION_DATE_INVALID);
            else
                if (education.getEndedAt() == null)
                    throw new CustomException(EDUCATION_ENDED_AT_FIELD_REQUIRED);
        }

        for (WorkDefaultReqDto work : works) {
            if (work.getIsCurrent())
                if (work.getStartedAt().isAfter(work.getEndedAt()))
                    throw new CustomException(WORK_DATE_INVALID);
            else
                if (work.getEndedAt() == null)
                    throw new CustomException(WORK_ENDED_AT_FIELD_REQUIRED);
        }
    }
}
