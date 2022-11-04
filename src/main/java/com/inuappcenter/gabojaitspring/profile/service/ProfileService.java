package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.exception.http.NotFoundException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.*;
import com.inuappcenter.gabojaitspring.profile.repository.ProfileRepository;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final EducationService educationService;

    /**
     * 프로필 저장 |
     * 프로필을 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public ProfileDefaultResponseDto save(ProfileSaveRequestDto request) {
        log.info("INITIALIZE | 프로필 저장 At " + LocalDateTime.now() + " | " + request.getUserId());
        Profile profile = null;
        try {
            profile = profileRepository.save(request.toEntity());
            userService.saveProfile(request.getUserId(), profile.getId());
        } catch (Exception e) {
            throw new InternalServerErrorException("프로필 저장 중 에러 발생", e);
        }

        List<EducationListResponseDto> educationList = listEducation(profile.getEducation());

        log.info("COMPLETE | 프로필 저장 At " + LocalDateTime.now() +
                " | userId = " + profile.getUserId() + ", profileId = " + profile.getId());

        return new ProfileDefaultResponseDto(profile, educationList);
    }

    /**
     * 프로필 수정 |
     * 프로필을 수정한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public ProfileDefaultResponseDto update(ProfileUpdateRequestDto request) {
        log.info("INITIALIZE | 프로필 수정 At " + LocalDateTime.now() + " | " + request.getId());
        Profile profile = findProfile(request.getId());
        try {
            profile.update(request.getAbout(), request.getPosition());
            profileRepository.save(profile);
        } catch (Exception e) {
            throw new InternalServerErrorException("프로필 수정 중 에러 발생", e);
        }

        List<EducationListResponseDto> educationList = listEducation(profile.getEducation());

        log.info("COMPLETE | 프로필 수정 At " + LocalDateTime.now() + " | " + request.getId());
        return new ProfileDefaultResponseDto(profile, educationList);
    }

    /**
     * 학력 리스트 조회 |
     * 학력 리스트를 조회하고 DTO 형태로 반환한다.
     */
    public List<EducationListResponseDto> listEducation(List<Education> educationList) {
        log.info("INITIALIZE | 학력 리스트 조회 At " + LocalDateTime.now());
        List<EducationListResponseDto> list = new LinkedList<>();
        for (Education education : educationList) {
            list.add(new EducationListResponseDto(education));
        }
        log.info("COMPLETE | 학력 리스트 조회 At " + LocalDateTime.now());
        return list;
    }

    /**
     * 프로필 조희 |
     * 프로필을 조회한다. 조회가 되지 않을 경우 404(NotFound)를 던진다.
     */
    public ProfileDefaultResponseDto findOneProfile(String id) {
        log.info("INITIALIZE | 프로필 조회 At " + LocalDateTime.now() + " | " + id);
        Profile foundProfile = profileRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재 하지 않은 정보입니다");
                });

        List<EducationListResponseDto> educationList = listEducation(foundProfile.getEducation());

        ProfileDefaultResponseDto profile = new ProfileDefaultResponseDto(foundProfile, educationList);
        log.info("COMPLETE | 프로필 조회 At " + LocalDateTime.now() + " | " + id);
        return profile;
    }

    /**
     * 프로필 조희 후 프로필 엔티티 반환 |
     * 프로필을 조회 하여 프로필 엔티티로 반환한다. 조회가 되지 않을 경우 404(NotFound)를 던진다.
     */
    public Profile findProfile(String id) {
        log.info("INITIALIZE | 프로필 조회 후 프로필 엔티티 반환 At " + LocalDateTime.now() + " | " + id);
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재 하지 않은 정보입니다");
                });
        log.info("COMPLETE | 프로필 조회 후 프로필 엔티티 반환 At " + LocalDateTime.now() + " | " + id);
        return profile;
    }

    /**
     * 프로필 학력 저장 |
     * 프로필 학력을 저장한다.
     */
    public void saveEducation(EducationSaveRequestDto request) {
        log.info("INITIALIZE | 프로필 학력 저장 At " + LocalDateTime.now() + " | " + request.getUserId());
        educationService.save(request);
        log.info("INITIALIZE | 프로필 학력 저장 At " + LocalDateTime.now() + " | " + request.getUserId());
    }

    /**
     * 프로필 학력 수정 |
     * 프로필 학력을 수정한다.
     */
    public void updateEducation(EducationUpdateRequestDto request) {
        log.info("INITIALIZE | 프로필 학력 수정 At " + LocalDateTime.now() + " | " + request.getId());
        educationService.update(request);
        log.info("INITIALIZE | 프로필 학력 수정 At " + LocalDateTime.now() + " | " + request.getId());
    }

    public void deleteEducation(EducationDeleteRequestDto request) {
        log.info("INITIALIZE | 프로필 학력 삭제 At " + LocalDateTime.now() +
                " | profileId = " + request.getProfileId() + " educationId = " + request.getEducationId());
        Profile profile = findProfile(request.getProfileId());
        try {

            for (Education education : profile.getEducation())
                if (education.getId().equals(request.getEducationId())) {
                    educationService.delete(education.getId());
                    profile.removeEducation(education);
                }
            profile = profileRepository.save(profile);
        } catch (Exception e) {
            throw new InternalServerErrorException("프로필 학력 삭제", e);
        }
        log.info("INITIALIZE | 프로필 학력 삭제 At " + LocalDateTime.now() + " | profileId = " + profile.getId());
    }
}
