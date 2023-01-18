package com.inuappcenter.gabojaitspring.project.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.project.domain.Apply;
import com.inuappcenter.gabojaitspring.project.dto.ApplySaveRequestDto;
import com.inuappcenter.gabojaitspring.project.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.NON_EXISTING_APPLY;
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

    /**
     * 지원 단건 조회 |
     * 단건 지원 정보를 식별자로 조회한다. |
     * 404: 지원 정보 존재하지 않은 에러
     */
    public Apply findOne(ObjectId applyId) {
        log.info("INITIALIZE | ApplyService | findOne | " + applyId);
        LocalDateTime initTime = LocalDateTime.now();

        Apply apply = applyRepository.findById(applyId)
                .orElseThrow(() -> {
                    throw new CustomException(NON_EXISTING_APPLY);
                });

        log.info("COMPLETE | ApplyService | findOne | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                apply.getId());
        return apply;
    }

    /**
     * 지원 수락 또는 거절 |
     * 지원을 수락하거나 거절하고 정보를 저장한다. |
     * 500: 지원 정보 저장 중 서버 에러
     */
    public Apply acceptOrDecline(Apply apply, Boolean isAccepted) {
        log.info("INITIALIZE | ApplyService | acceptOrDecline | " + apply.getId() + " | " + isAccepted);
        LocalDateTime initTime = LocalDateTime.now();

        apply.setIsAccepted(isAccepted);

        try {
            apply = applyRepository.save(apply);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ApplyService | acceptOrDecline | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + apply.getId());
        return apply;
    }
}
