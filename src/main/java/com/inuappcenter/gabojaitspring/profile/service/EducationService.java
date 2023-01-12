package com.inuappcenter.gabojaitspring.profile.service;


import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.EducationSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.EducationUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;

    /**
     * 학력 생성 |
     * 학력 생성 절차를 밟아서 정보를 저장한다. |
     * 500: 학력 정보 저장 중 서버 에러
     */
    public Education save(EducationSaveRequestDto request, Profile profile) {
        log.info("INITIALIZE | EducationService | save | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        validateDate(request.getStartedDate(), request.getEndedDate());

        Education education = request.toEntity(profile.getId());

        try {
            education = educationRepository.save(education);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | EducationService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + education.getId());
        return education;
    }

    /**
     * 날짜 검증 |
     * 시작일이 종료일 이전으로 설정되어 있는지 확인한다. |
     * 400: 종료일이 시작일보다 전일 경우 에러
     */
    private void validateDate(LocalDate startedDate, LocalDate endedDate) {
        log.info("PROGRESS | EducationService | validateDate | " + startedDate.toString() + " | " +
                endedDate.toString());

        if (startedDate.isAfter(endedDate))
            throw new CustomException(DATE_INCORRECT);
    }

    /**
     * 학력 업데이트 |
     * 학력 정보를 조회하여 업데이트한다. |
     * 500: 학력 정보 저장 중 서버 에러
     */
    public void update(Profile profile, EducationUpdateRequestDto request) {
        log.info("INITIALIZE | EducationService | update | " + profile.getId() + " | " + request.getEducationId());
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId educationId = new ObjectId(request.getEducationId());
        Education education = findOne(profile, educationId);
        validateDate(request.getStartedDate(), request.getEndedDate());

        education.updateEducation(request.getInstitutionName(),
                request.getStartedDate(),
                request.getEndedDate(),
                request.getIsCurrent());

        try {
            education = educationRepository.save(education);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | EducationService | update | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + education.getId());
    }

    /**
     * 학력 단건 조회 |
     * 학력 정보가 프로필 정보에 있는지 확인하고 반환한다. |
     * 404: 존재하지 않은 학력 정보 에러
     */
    private Education findOne(Profile profile, ObjectId educationId) {
        log.info("PROGRESS | EducationService | findOne | " + profile.getId() + " | " + educationId);

        for (Education education : profile.getEducations()) {
            if (education.getId().equals(educationId)) {
                return education;
            }
        }

        throw new CustomException(NON_EXISTING_EDUCATION);
    }

    /**
     * 학력 제거 |
     * 학력 정보에 제거 표시를 한 후 학력을 반환한다. |
     * 500: 학력 정보 저장 중 서버 에러
     */
    public Education delete(Profile profile, String educationId) {
        log.info("INITIALIZE | EducationService | delete | " + profile.getId() + " | " + educationId);
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId id = new ObjectId(educationId);
        Education education = findOne(profile, id);

        education.deleteEducation();

        try {
            educationRepository.save(education);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | EducationService | delete | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                education.getId() + " | " + education.getIsDeleted());
        return education;
    }
}
