package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.common.util.FileProvider;
import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.favorite.repository.FavoriteUserRepository;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.profile.domain.*;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.dto.*;
import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileDefaultResDto;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileOfferAndFavoriteResDto;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileSeekResDto;
import com.gabojait.gabojaitspring.profile.repository.EducationRepository;
import com.gabojait.gabojaitspring.profile.repository.PortfolioRepository;
import com.gabojait.gabojaitspring.profile.repository.SkillRepository;
import com.gabojait.gabojaitspring.profile.repository.WorkRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.repository.TeamMemberRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
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
    private final TeamMemberRepository teamMemberRepository;
    private final FileProvider fileProvider;
    private final PageProvider pageProvider;

    /**
     * 프로필 업데이트 |
     * 400(EDUCATION_DATE_INVALID / EDUCATION_ENDED_AT_FIELD_REQUIRED / WORK_DATE_INVALID /
     * WORK_ENDED_AT_FIELD_REQUIRED)
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public ProfileDefaultResDto updateProfile(long userId, ProfileDefaultReqDto request) {
        User user = findOneUser(userId);

        validateDate(request.getEducations(), request.getWorks());

        updatePosition(user, Position.valueOf(request.getPosition()));
        commandEducations(user, request.getEducations());
        commandPortfolios(user, request.getPortfolios());
        commandSkills(user, request.getSkills());
        commandWorks(user, request.getWorks());

        ProfileInfoDto profileInfo = findAllProfileInfo(user);

        return new ProfileDefaultResDto(user, profileInfo);
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
    public ProfileDefaultResDto uploadProfileImage(long userId, MultipartFile multipartFile) {
        User user = findOneUser(userId);

        String url = fileProvider.upload(profileImgBucketName,
                user.getId().toString(),
                UUID.randomUUID().toString(),
                multipartFile,
                true);

        user.updateImageUrl(url);

        ProfileInfoDto profileInfo = findAllProfileInfo(user);

        return new ProfileDefaultResDto(user, profileInfo);
    }

    /**
     * 프로필 이미지 삭제 |
     * 404(USER_NOT_FOUND)
     */
    public ProfileDefaultResDto deleteProfileImage(long userId) {
        User user = findOneUser(userId);
        user.updateImageUrl(null);

        ProfileInfoDto profileInfo = findAllProfileInfo(user);

        return new ProfileDefaultResDto(user, profileInfo);
    }

    /**
     * 회원 식별자로 회원으로 타 회원 프로필 단건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public ProfileOfferAndFavoriteResDto findOneOtherProfile(long userId, Long otherUserId) {
        User user = findOneUser(userId);
        ProfileInfoDto profileInfo = findAllProfileInfo(user);

        if (userId == otherUserId)
            return new ProfileOfferAndFavoriteResDto(user, profileInfo, List.of(), null);

        User otherUser = findOneUser(otherUserId);

        otherUser.incrementVisitedCnt();

        List<Offer> offers = new ArrayList<>();
        Boolean isFavorite = null;

        Optional<TeamMember> foundTeamMember = findOneCurrentTeamMember(user);
        TeamMember teamMember = validateHasCurrentTeam(foundTeamMember);

        if (teamMember.getIsLeader()) {
            offers = findAllOffersToUser(user, otherUser);
            isFavorite = isFavoriteUser(otherUser, user);
        }

        return new ProfileOfferAndFavoriteResDto(otherUser, profileInfo, offers, isFavorite);
    }

    /**
     * 기술 저장 |
     * 500(SERVER_ERROR)
     */
    public void saveSkill(Skill skill) {
        try {
            skillRepository.save(skill);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 학력 저장 |
     * 500(SERVER_ERROR)
     */
    public void saveEducation(Education education) {
        try {
            educationRepository.save(education);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 경력 저장 |
     * 500(SERVER_ERROR)
     */
    public void saveWork(Work work) {
        try {
            workRepository.save(work);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 저장 |
     * 500(SERVER_ERROR)
     */
    public void savePortfolio(Portfolio portfolio) {
        try {
            portfolioRepository.save(portfolio);
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
    public ProfileSeekPageDto findManyUsersByPosition(long userId,
                                                      Position position,
                                                      long pageFrom,
                                                      Integer pageSize) {
        User user = findOneUser(userId);

        pageFrom = pageProvider.validatePageFrom(pageFrom);
        Pageable pageable = pageProvider.validatePageable(pageSize, 20);

        Page<User> users;

        if (!position.equals(Position.NONE))
            users = findManyUsersByPositionOrderByCreatedAt(pageFrom, position, pageable);
        else
            users = findManyUsersOrderByCreatedAt(pageFrom, pageable);

        List<ProfileSeekResDto> profileSeekResDtos = new ArrayList<>();
        Optional<TeamMember> foundTeamMember = findOneCurrentTeamMember(user);
        TeamMember teamMember = validateHasCurrentTeam(foundTeamMember);

        if (teamMember.getIsLeader()) {
            for (User u : users) {
                List<Offer> offers = findAllOffersToUser(user, u);
                profileSeekResDtos.add(new ProfileSeekResDto(u, offers));
            }
        } else {
            for (User u : users)
                profileSeekResDtos.add(new ProfileSeekResDto(u, List.of()));
        }

        return new ProfileSeekPageDto(profileSeekResDtos, users.getTotalElements());
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneUser(long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 식별자로 회원 프로필 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    public ProfileDefaultResDto findOneProfile(long userId) {
        User user = findOneUser(userId);

        ProfileInfoDto profileInfo = findAllProfileInfo(user);

        return new ProfileDefaultResDto(user, profileInfo);
    }

    /**
     * 전체 포지션을 생성순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersOrderByCreatedAt(long userId, Pageable pageable) {
        try {
            return userRepository.searchOrderByCreatedAt(userId, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 생성순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersByPositionOrderByCreatedAt(long userId, Position position, Pageable pageable) {
        try {
            return userRepository.searchByPositionOrderByCreatedAt(userId, position, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 리더와 회원으로 리더가 특정 회원에게 보낸 전체 제안 조회 |
     * 404(TEAM_MEMBER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    private List<Offer> findAllOffersToUser(User leader, User user) {
        Optional<TeamMember> foundLeaderTeamMember = findOneCurrentTeamMember(leader);
        TeamMember leaderTeamMember = validateHasCurrentTeam(foundLeaderTeamMember);
        validateLeader(leaderTeamMember);
        Team team = leaderTeamMember.getTeam();

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
     * 회원으로 학력, 포트폴리오, 기술, 경력, 팀 전체 조회 |
     * 500(SERVER_ERROR)
     */
    private ProfileInfoDto findAllProfileInfo(User user) {
        List<Education> educations = findAllEducation(user);
        List<Portfolio> portfolios = findAllPortfolio(user);
        List<Skill> skills = findAllSkill(user);
        List<Work> works = findAllWork(user);
        List<TeamMember> teamMembers = findAllTeamMembers(user);

        return ProfileInfoDto.builder()
                .educations(educations)
                .portfolios(portfolios)
                .skills(skills)
                .works(works)
                .teamMembers(teamMembers)
                .build();
    }

    /**
     * 회원으로 팀원 단건 조회
     */
    private Optional<TeamMember> findOneCurrentTeamMember(User user) {
        return teamMemberRepository.findByUserAndIsQuitIsFalseAndIsDeletedIsFalse(user);
    }

    /**
     * 회원으로 전체 팀원 조회
     */
    private List<TeamMember> findAllTeamMembers(User user) {
        return teamMemberRepository.findAllByUserAndIsQuitIsFalse(user);
    }

    /**
     * 찜한 회원 여부 확인 |
     * 404(TEAM_MEMBER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    private boolean isFavoriteUser(User user, User leader) {
        Optional<TeamMember> foundLeaderTeamMember = findOneCurrentTeamMember(leader);
        TeamMember leaderTeamMember = validateHasCurrentTeam(foundLeaderTeamMember);
        validateLeader(leaderTeamMember);
        Team team = leaderTeamMember.getTeam();

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
     * 기술들 생성, 수정, 삭제 |
     * 500(SERVER_ERROR)
     */
    private void commandSkills(User user, List<SkillDefaultReqDto> requests) {
        List<Skill> currentSkills = findAllSkill(user);

        if (requests.isEmpty()) {
            currentSkills.forEach(this::softDeleteSkill);
            return;
        }

        for (SkillDefaultReqDto request : requests) {
            if (request.getSkillId() == null)
                saveSkill(request.toEntity(user));
            else
                for (Skill currentSkill : currentSkills)
                    if (request.getSkillId().equals(currentSkill.getId())) {
                        if (request.hashCode(user) != currentSkill.hashCode())
                            currentSkill.update(request.getSkillName(),
                                    request.getIsExperienced(),
                                    Level.valueOf(request.getLevel()));

                        currentSkills.remove(currentSkill);
                        break;
                    }
        }

        currentSkills.forEach(this::softDeleteSkill);
    }

    /**
     * 학력들 생성, 수정, 삭제 |
     * 500(SERVER_ERROR)
     */
    private void commandEducations(User user, List<EducationDefaultReqDto> requests) {
        List<Education> currentEducations = findAllEducation(user);

        if (requests.isEmpty()) {
            currentEducations.forEach(this::softDeleteEducation);
            return;
        }

        for (EducationDefaultReqDto request : requests) {
            if (request.getEducationId() == null)
                saveEducation(request.toEntity(user));
            else
                for (Education currentEducation : currentEducations)
                    if (request.getEducationId().equals(currentEducation.getId())) {
                        if (request.hashCode(user) != currentEducation.hashCode())
                            currentEducation.update(request.getInstitutionName(),
                                    request.getStartedAt(),
                                    request.getEndedAt(),
                                    request.getIsCurrent());

                        currentEducations.remove(currentEducation);
                        break;
                    }
        }

        currentEducations.forEach(this::softDeleteEducation);
    }

    /**
     * 경력들 생성, 수정, 삭제 |
     * 500(SERVER_ERROR)
     */
    private void commandWorks(User user, List<WorkDefaultReqDto> requests) {
        List<Work> currentWorks = findAllWork(user);

        if (requests.isEmpty()) {
            currentWorks.forEach(this::softDeleteWork);
            return;
        }

        for (WorkDefaultReqDto request : requests) {
            if (request.getWorkId() == null)
                saveWork(request.toEntity(user));
            else
                for (Work currentWork : currentWorks)
                    if (request.getWorkId().equals(currentWork.getId())) {
                        if (request.hashCode(user) != currentWorks.hashCode())
                            currentWork.update(request.getCorporationName(),
                                    request.getWorkDescription(),
                                    request.getStartedAt(),
                                    request.getEndedAt(),
                                    request.getIsCurrent());

                        currentWorks.remove(currentWork);
                        break;
                    }
        }
    }

    /**
     * 포트폴리오 업데이트 |
     * 500(SERVER_ERROR)
     */
    private void commandPortfolios(User user, List<PortfolioDefaultReqDto> requests) {
        List<Portfolio> currentPortfolios = findAllPortfolio(user);

        if (requests.isEmpty()) {
            currentPortfolios.forEach(this::softDeletePortfolio);
            return;
        }

        for (PortfolioDefaultReqDto request : requests) {
            if (request.getPortfolioId() == null)
                savePortfolio(request.toEntity(user));
            else for (Portfolio currentPortfolio : currentPortfolios)
                if (request.getPortfolioId().equals(currentPortfolio.getId())
                        && request.hashCode(user) != currentPortfolio.hashCode()) {
                    currentPortfolio.update(request.getPortfolioName(),
                            request.getPortfolioUrl(),
                            Media.valueOf(request.getMedia()));
                    currentPortfolios.remove(currentPortfolio);
                    break;
                }
        }

        currentPortfolios.forEach(this::softDeletePortfolio);
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

    /**
     * 현재 팀 존재 여부 검증 |
     * 404(TEAM_MEMBER_NOT_FOUND)
     */
    private TeamMember validateHasCurrentTeam(Optional<TeamMember> teamMember) {
        if (teamMember.isEmpty())
            throw new CustomException(TEAM_MEMBER_NOT_FOUND);

        return teamMember.get();
    }

    /**
     * 리더 여부 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateLeader(TeamMember teamMember) {
        if (!teamMember.getIsLeader())
            throw new CustomException(REQUEST_FORBIDDEN);
    }
}
