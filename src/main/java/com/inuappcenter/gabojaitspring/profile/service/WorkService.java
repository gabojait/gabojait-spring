package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
import com.inuappcenter.gabojaitspring.profile.dto.req.WorkDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.repository.WorkRepository;
import com.inuappcenter.gabojaitspring.user.domain.User;
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
    public Work save(Work work) {

        try {
            return workRepository.save(work);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 식별자 경력 조회 |
     * 404(WORK_NOT_FOUND)
     */
    public Work findOne(String workId) {

        return workRepository.findById(new ObjectId(workId))
                .orElseThrow(() -> {
                    throw new CustomException(WORK_NOT_FOUND);
                });
    }

    /**
     * 경력 업데이트 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void update(Work work, WorkDefaultReqDto request) {

        try {
            work.update(request.getCorporationName(),
                    request.getStartedDate(),
                    request.getEndedDate(),
                    request.getIsCurrent(),
                    request.getDescription());
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(work);
    }

    /**
     * 권한 검증 |
     * 403(ROLE_NOT_ALLOWED)
     */
    public void validateOwner(Work work, User user) {

        if (!user.getWorks().contains(work))
            throw new CustomException(ROLE_NOT_ALLOWED);
    }

    /**
     * 경력 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void delete(Work work) {

        try {
            work.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(work);
    }
}
