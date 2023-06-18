package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.dto.req.WorkCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.WorkUpdateReqDto;
import com.gabojait.gabojaitspring.profile.repository.WorkRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;

    /**
     * 경력 생성 |
     * 500(SERVER_ERROR)
     */
    public void create(User user, WorkCreateReqDto request) {
        Work work = request.toEntity(user);

        saveWork(work);
    }

    /**
     * 경력 업데이트 |
     * 404(WORK_NOT_FOUND)
     */
    public void update(User user, WorkUpdateReqDto request) {
        Work work = findOneWork(request.getWorkId(), user);

        work.update(request.getCorporationName(),
                request.getWorkDescription(),
                request.getStartedAt(),
                request.getEndedAt(),
                request.getIsCurrent());
    }

    /**
     * 경력 삭제 |
     * 404(WORK_NOT_FOUND)
     */
    public void delete(User user, Long workId) {
        Work work = findOneWork(workId, user);

        softDeleteWork(work);
    }

    /**
     * 경력 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveWork(Work work) {
       try {
           workRepository.save(work);
       } catch (RuntimeException e) {
           throw new CustomException(e, SERVER_ERROR);
       }
    }

    /**
     * 경력 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    private void softDeleteWork(Work work) {
        work.delete();
    }

    /**
     * 식별자와 회원으로 경력 단건 조회 |
     * 404(WORK_NOT_FOUND)
     */
    private Work findOneWork(Long workId, User user) {
        return workRepository.findByIdAndUserAndIsDeletedIsFalse(workId, user)
                .orElseThrow(() -> {
                    throw new CustomException(WORK_NOT_FOUND);
                });
    }
}
