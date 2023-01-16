package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.file.service.FileService;
import com.inuappcenter.gabojaitspring.profile.domain.*;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.ProfileRepository;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    @Value(value = "${s3.profileImgBucketName}")
    private String bucketName;
    private final ProfileRepository profileRepository;
    private final FileService fileService;


    /**
     * 프로필 생성 |
     * 프로필 생성 절차를 밟아서 정보를 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile save(ProfileSaveRequestDto request) {
        log.info("INITIALIZE | ProfileService | save");
        LocalDateTime initTime = LocalDateTime.now();

        Position position = validatePosition(request.getPosition());

        Profile profile = request.toEntity(position);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId());
        return profile;
    }

    /**
     * 포지션 검증 |
     * 포지션이 디자이너 'D', 백엔드 'B', 프론트엔드 'F', 매니저 'M'로 되어 있는지 확인한다. |
     * 400: 올바르지 않은 포맷
     */
    private Position validatePosition(Character position) {
        log.info("PROGRESS | ProfileService | validatePosition | " + position);

        if (position == Position.DESIGNER.getType()) {
            return Position.DESIGNER;
        } else if (position == Position.BACKEND.getType()) {
            return Position.BACKEND;
        } else if (position == Position.FRONTEND.getType()) {
            return Position.FRONTEND;
        } else if (position == Position.MANAGER.getType()) {
            return Position.MANAGER;
        } else {
            throw new CustomException(POSITION_INCORRECT_TYPE);
        }
    }

    /**
     * 프로필에 유저 아이디와 닉네임 저장 |
     * 유저 아이디와 닉네임을 프로필 정보에 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public void saveUser(User user, Profile profile) {
        log.info("INITIALIZE | ProfileService | saveUserId | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.setUser(user);

        try {
            profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | saveUserId | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + profile.getUserId());
    }

    /**
     * 프로필 단건 조회 |
     * 프로필 정보를 찾아 반환한다. |
     * 404: 존재하지 않은 프로필 에러
     */
    public Profile findOne(ObjectId profileId) {
        log.info("INITIALIZE | ProfileService | findOne | " + profileId);
        LocalDateTime initTime = LocalDateTime.now();

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> {
                    throw new CustomException(NON_EXISTING_PROFILE);
                });

        log.info("COMPLETE | ProfileService | findOne | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + profile.getUserId());
        return profile;
    }

    /**
     * 프로필 다건 조회 |
     * 여러 프로필 정보를 찾아 반환한다. |
     * 404: 존재하지 않은 프로필 에러
     */
    public List<Profile> findMany(List<ObjectId> profileIds) {
        log.info("INITIALIZE | ProfileService | findMany | " + profileIds.size());
        LocalDateTime initTime = LocalDateTime.now();

        List<Profile> profiles = new ArrayList<>();
        for(ObjectId profileId : profileIds)
            profiles.add(profileRepository.findById(profileId)
                    .orElseThrow(() -> {
                        throw new CustomException(NON_EXISTING_PROFILE);
                    }));

        log.info("COMPLETE | ProfileService | findMany | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profileIds.size());
        return profiles;
    }

    /**
     * 프로필 수정 |
     * 프로필 정보를 수정한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile updateProfile(Profile profile, ProfileUpdateRequestDto request) {
        log.info("INITIALIZE | ProfileService | updateProfile | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        Position position = validatePosition(request.getPosition());

        profile.updateProfile(request.getDescription(), position);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | updateProfile | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + profile.getUserId());
        return profile;
    }

    /**
     * 프로필 사진 수정 |
     * 프로필 정보를 수정한다.
     * 500: 프로필 사진 저장 중 서버 에러
     */
    public Profile updateImage(String username, Profile profile, MultipartFile image) {
        log.info("INITIALIZE | ProfileService | updateImage | " + username + " | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        validateImageType(image);

        String imageUrl = fileService.upload(bucketName,
                username + "-" + profile.getId().toString(),
                initTime.format(DateTimeFormatter.ISO_DATE_TIME),
                image);

        profile.setImageUrl(imageUrl);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(PROFILE_IMG_TYPE_UNSUPPORTED);
        }

        log.info("COMPLETE | ProfileService | updateImage | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + profile.getUserId());
        return profile;
    }

    /**
     * 이미지 타입 검증 |
     * 이미지 타입이 .png, .jpg, .jpeg 중 하나로 되어 있는지 확인한다. |
     * 415: 올바르지 않은 포맷
     */
    private void validateImageType(MultipartFile image) {
        log.info("PROGRESS | ProfileService | validateImageType");

        String type = image.getContentType().split("/")[1];

        if (!(type.equals("jpg") || type.equals("jpeg") || type.equals("png"))) {
            throw new CustomException(PROFILE_IMG_TYPE_UNSUPPORTED);
        }
    }

    /**
     * 학력 프로필 생성 |
     * 학력 정보를 프로필에 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile saveEducation(Profile profile, Education education) {
        log.info("INITIALIZE | ProfileService | saveEducation | " + profile.getId() + " | " + education.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.addEducation(education);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | saveEducation | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + education.getId());
        return profile;
    }

    /**
     * 학력 프로필 제거 |
     * 프로필에서 학력을 제거한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile deleteEducation(Profile profile, Education education) {
        log.info("INITIALIZE | ProfileService | deleteEducation | " + profile.getId() + " | " + education.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.removeEducation(education);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | deleteEducation | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + profile.getUserId());
        return profile;
    }

    /**
     * 기술 프로필 생성 |
     * 기술 정보를 프로필에 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile saveSkill(Profile profile, Skill skill) {
        log.info("INITIALIZE | ProfileService | saveSkill | " + profile.getId() + " | " + skill.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.addSkill(skill);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | saveSkill | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + skill.getId());
        return profile;
    }

    /**
     * 기술 프로필 제거 |
     * 프로필에서 기술을 제거한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile deleteSkill(Profile profile, Skill skill) {
        log.info("INITIALIZE | ProfileService | deleteSkill | " + profile.getId() + " | " + skill.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.removeSkill(skill);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | deleteSkill | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + profile.getUserId());
        return profile;
    }

    /**
     * 경력 프로필 생성 |
     * 경력 정보를 프로필에 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile saveWork(Profile profile, Work work) {
        log.info("INITIALIZE | ProfileService | saveWork | " + profile.getId() + " | " + work.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.addWork(work);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | saveWork | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + work.getId());
        return profile;
    }

    /**
     * 경력 프로필 제거 |
     * 프로필에서 경력을 제거한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile deleteWork(Profile profile, Work work) {
        log.info("INITIALIZE | ProfileService | deleteWork | " + profile.getId() + " | " + work.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.removeWork(work);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | deleteWork | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + profile.getUserId());
        return profile;
    }

    /**
     * 포트폴리오 프로필 생성 |
     * 포트폴리오 정보를 프로필에 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile savePortfolio(Profile profile, Portfolio portfolio) {
        log.info("INITIALIZE | ProfileService | savePortfolio | " + profile.getId() + " | " + portfolio.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.addPortfolio(portfolio);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | savePortfolio | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + portfolio.getId());
        return profile;
    }

    /**
     * 포트폴리오 프로필 제거 |
     * 프로필에서 포트폴리오를 제거한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile deletePortfolio(Profile profile, Portfolio portfolio) {
        log.info("INITIALIZE | ProfileService | deletePortfolio | " + profile.getId() + " | " + portfolio.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.removePortfolio(portfolio);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | deletePortfolio | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + profile.getUserId());
        return profile;
    }

    /**
     * 프로젝트 시작 정보 프로필에 저장 |
     * 프로필에 새 프로젝트를 시작한다는 것을 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile startProject(Profile profile, Project project) {
        log.info("INITIALIZE | ProfileService | startProject | " + profile.getId() + project.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.startProject(project);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | startProject | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + project.getId());
        return profile;
    }

    /**
     * 프로젝트 종료 정보 프로필에 저장 |
     * 프로필에 프로젝트를 종료한다는 것을 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile endProject(Profile profile, Project project) {
        log.info("INITIALIZE | ProfileService | endProject | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.endProject();

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | endProject | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + profile.getId() + " | " + project.getId());
        return profile;
    }
}
