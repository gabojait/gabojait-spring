package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
import com.inuappcenter.gabojaitspring.profile.dto.req.WorkDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkService {

    private final WorkRepository workRepository;

    /**
     * 경력 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Work saveWork(ObjectId userId, WorkDefaultReqDto request) {

        try {
            return workRepository.save(request.toEntity(userId));
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 식별자 경력 조회 |
     * 404(WORK_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Work findOneWork(String workId) {

        try {
            return workRepository.findById(new ObjectId(workId))
                    .orElseThrow(() -> {
                        throw new CustomException(WORK_NOT_FOUND);
                    });
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 경력 업데이트 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void updateWork(Work work, WorkDefaultReqDto request) {

        try {
            work.update(request.getCorporationName(),
                    request.getStartedDate(),
                    request.getEndedDate(),
                    request.getIsCurrent(),
                    request.getDescription());
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 경력 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void deleteWork(Work work) {

        try {
            work.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
