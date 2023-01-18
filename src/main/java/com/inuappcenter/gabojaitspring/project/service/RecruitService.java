package com.inuappcenter.gabojaitspring.project.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import com.inuappcenter.gabojaitspring.project.domain.Recruit;
import com.inuappcenter.gabojaitspring.project.dto.RecruitSaveRequestDto;
import com.inuappcenter.gabojaitspring.project.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.NON_EXISTING_RECRUIT;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitService {

    private  final RecruitRepository recruitRepository;

    /**
     * 영입 |
     * 회원에게 프로젝트로 영입 요청 절차를 밟아서 정보를 저장한다. |
     * 500: 영입 정보 저장 중 서버 에러
     */
    public Recruit save(RecruitSaveRequestDto request, Project project, Position position) {
        log.info("INITIALIZE | RecruitService | save | " + project.getId() + " | " + request.getUserProfileId() + " | " +
                position.getType());
        LocalDateTime initTime = LocalDateTime.now();

        Recruit recruit = request.toEntity(project.getId(), position);

        try {
            recruit = recruitRepository.save(recruit);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | RecruitService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                project.getId() + " | " + recruit.getUserProfileId());
        return recruit;
    }

    /**
     * 영입 단건 조회 |
     * 단건 영입 정보를 식별자로 조회한다. |
     * 404: 영입 정보 존재하지 않은 에러
     */
    public Recruit findOne(ObjectId recruitId) {
        log.info("INITIALIZE | RecruitService | findOne | " + recruitId);
        LocalDateTime initTime = LocalDateTime.now();

        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> {
                    throw new CustomException(NON_EXISTING_RECRUIT);
                });

        log.info("COMPLETE | RecruitService | findOne | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                recruit.getId());
        return recruit;
    }

    /**
     * 영입 수락 또는 거절 |
     * 영입을 수락하거나 거절하고 정보를 저장한다. |
     * 500: 영입 정보 저장 중 서버 에러
     */
    public Recruit acceptOrDecline(Recruit recruit, Boolean isAccepted) {
        log.info("INITIALIZE | RecruitService | acceptOrDecline | " + recruit.getId() + " | " + isAccepted);
        LocalDateTime initTime = LocalDateTime.now();

        recruit.setIsAccepted(isAccepted);

        try {
            recruit = recruitRepository.save(recruit);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | RecruitService | acceptOrDecline | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + recruit.getId());
        return recruit;
    }
}
