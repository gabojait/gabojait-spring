package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.dto.req.WorkCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.WorkUpdateReqDto;
import com.gabojait.gabojaitspring.profile.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;
    private final UtilityProvider utilityProvider;

    /**
     * 전체 경력 업데이트 | main |
     * 403(REQUEST_FORBIDDEN)
     * 404(WORK_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void updateAll(ObjectId userId, List<WorkUpdateReqDto> requests) {
        List<Work> works = new ArrayList<>();
        for (WorkUpdateReqDto request: requests) {
            ObjectId id = utilityProvider.toObjectId(request.getWorkId());

            Work work = findOne(id);
            validateOwner(work, userId);

            works.add(work);
        }

        for (int i = 0; i < works.size(); i++) {
            works.get(i).update(requests.get(i).getCorporationName(),
                    requests.get(i).getStartedDate(),
                    requests.get(i).getEndedDate(),
                    requests.get(i).getIsCurrent(),
                    requests.get(i).getWorkDescription());

            save(works.get(i));
        }
    }

    /**
     * 전체 경력 생성 | sub |
     * 500(SERVER_ERROR)
     */
    public List<Work> createAll(ObjectId userId, List<WorkCreateReqDto> requests) {
        List<Work> works = new ArrayList<>();
        for (WorkCreateReqDto request: requests) {
            Work work = save(request.toEntity(userId));
            works.add(work);
        }

        return works;
    }

    /**
     * 전체 경력 삭제 | sub |
     * 403(REQUEST_FORBIDDEN)
     * 404(WORK_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Work> deleteAll(ObjectId userId, List<String> workIds) {
        List<Work> works = new ArrayList<>();
        for (String workId: workIds) {
            ObjectId id = utilityProvider.toObjectId(workId);

            Work work = findOne(id);
            validateOwner(work, userId);

            works.add(work);
        }

        for (Work work: works)
            softDelete(work);

        return works;
    }

    /**
     * 소유자 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateOwner(Work work, ObjectId userId) {
        if (!work.getUserId().equals(userId))
            throw new CustomException(null, REQUEST_FORBIDDEN);
    }

    /**
     * 경력 단건 조회 |
     * 404(WORK_NOT_FOUND)
     */
    private Work findOne(ObjectId workId) {
        return workRepository.findById(workId)
                .orElseThrow(() -> {
                    throw new CustomException(null, WORK_NOT_FOUND);
                });
    }

    /**
     * 경력 저장 |
     * 500(SERVER_ERROR)
     */
    public Work save(Work work) {
        try {
            return workRepository.save(work);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 경력 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    public void softDelete(Work work) {
        work.delete();

        save(work);
    }

    /**
     * 경력 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    private void hardDelete(Work work) {
        try {
            workRepository.delete(work);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
