package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
import com.inuappcenter.gabojaitspring.profile.dto.WorkSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.WorkUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.WorkRepository;
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
public class WorkService {

    private final WorkRepository workRepository;

    /**
     * 경력 생성 |
     * 경력 생성 절차를 밟아서 정보를 저장한다. |
     * 500: 경력 정보 저장 중 서버 에러
     */
    public Work save(WorkSaveRequestDto request, Profile profile) {
        log.info("INITIALIZE | WorkService | save | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        Work work = request.toEntity(profile.getId());

        try {
            work = workRepository.save(work);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | WorkService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + work.getId());
        return work;
    }

    /**
     * 경력 업데이트 |
     * 경력 정보를 조회하여 업데이트한다. |
     * 500: 경력 정보 저장 중 서버 에러
     */
    public void update(Profile profile, WorkUpdateRequestDto request) {
        log.info("INITIALIZE | WorkService | update | " + profile.getId() + " | " + request.getWorkId());
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId educationId = new ObjectId(request.getWorkId());
        Work work = findOne(profile, educationId);

        work.updateWork(request.getCorporationName(),
                request.getStartedDate(),
                request.getEndedDate(),
                request.getIsCurrent(),
                request.getDescription());

        try {
            work = workRepository.save(work);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | WorkService | update | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + work.getId());
    }

    /**
     * 경력 단건 조회 |
     * 경력 정보가 프로필 정보에 있는지 확인하고 반환한다. |
     * 404: 존재하지 않은 경력 정보 에러
     */
    private Work findOne(Profile profile, ObjectId workId) {
        log.info("PROGRESS | WorkService | findOne | " + profile.getId() + " | " + workId);

        for (Work work : profile.getWorks()) {
            if (work.getId().equals(workId)) {
                return work;
            }
        }

        throw new CustomException(NON_EXISTING_WORK);
    }

    /**
     * 경력 제거 |
     * 경력 정보에 제거 표시를 한 후 경력을 반환한다. |
     * 500: 경력 정보 저장 중 서버 에러
     */
    public Work delete(Profile profile, String workId) {
        log.info("INITIALIZE | EducationService | delete | " + profile.getId() + " | " + workId);
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId id = new ObjectId(workId);
        Work work = findOne(profile, id);

        work.deleteWork();

        try {
            workRepository.save(work);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | EducationService | delete | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                work.getId() + " | " + work.getIsDeleted());
        return work;
    }
}
