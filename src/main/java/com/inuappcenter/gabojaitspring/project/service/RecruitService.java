package com.inuappcenter.gabojaitspring.project.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import com.inuappcenter.gabojaitspring.project.domain.Recruit;
import com.inuappcenter.gabojaitspring.project.dto.RecruitSaveRequestDto;
import com.inuappcenter.gabojaitspring.project.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

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
        log.info("INITIALIZE | ApplyService | save | " + project.getId() + " | " + request.getUserProfileId() + " | " +
                position.getType());
        LocalDateTime initTime = LocalDateTime.now();

        Recruit recruit = request.toEntity(project.getId(), position);

        try {
            recruit = recruitRepository.save(recruit);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ApplyService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                project.getId() + " | " + recruit.getUserProfileId());
        return recruit;
    }
}
