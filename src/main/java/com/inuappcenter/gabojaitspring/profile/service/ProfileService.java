package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.*;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.ProfileRepository;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

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
     * 400: 올바르지 않을 포맷
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
            throw new CustomException(INCORRECT_POSITION_TYPE);
        }
    }

    /**
     * 유저 아이디 저장 |
     * 유저 아이디를 프로필 정보에 저장한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public void saveUserId(User user, Profile profile) {
        log.info("INITIALIZE | ProfileService | saveUserId | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        profile.setUserId(user.getId());

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
     * 프로필 수정 |
     * 프로필 정보를 수정한다. |
     * 500: 프로필 정보 저장 중 서버 에러
     */
    public Profile update(Profile profile, ProfileUpdateRequestDto request) {
        log.info("INITIALIZE | ProfileService | update | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        Position position = validatePosition(request.getPosition());

        profile.updateProfile(request.getDescription(), position);

        try {
            profile = profileRepository.save(profile);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProfileService | update | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + profile.getUserId());
        return profile;
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
}
