package com.inuappcenter.gabojaitspring.project.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.project.domain.Apply;
import com.inuappcenter.gabojaitspring.project.dto.ApplySaveRequestDto;
import com.inuappcenter.gabojaitspring.project.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applyRepository;

    /**
     * 지원 |
     * 프로젝트에 지원 요청 절차를 밟아서 정보를 저장한다. |
     * 500: 지원 정보 저장 중 서버 에러
     */
    public Apply save(ApplySaveRequestDto request, Profile profile, Position position) {
        log.info("INITIALIZE | ApplyService | save | " + profile.getId() + " | " + request.getProjectId() + " | " +
                position.getType());
        LocalDateTime initTime = LocalDateTime.now();

        Apply apply = request.toEntity(profile.getId(), position);

        try {
            apply = applyRepository.save(apply);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ApplyService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + request.getProjectId());
        return apply;
    }
}
