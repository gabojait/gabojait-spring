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
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(WORK_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Work> updateAll(ObjectId userId, List<WorkUpdateReqDto> requests) {
        List<Work> works = new ArrayList<>();

        for (WorkUpdateReqDto request : requests) {
            Work work = findOneById(request.getWorkId());
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

        return works;
    }

    /**
     * 계정 삭제 전 전체 삭제 | main |
     * 500(SERVER_ERROR)
     */
    public void deleteAllPreDeactivation(List<Work> works) {
        for (Work work : works)
            delete(work);
    }

    /**
     * 전체 경력 생성 | sub |
     * 500(SERVER_ERROR)
     */
    public List<Work> createAll(ObjectId userId, List<WorkCreateReqDto> requests) {
        List<Work> works = new ArrayList<>();

        for (WorkCreateReqDto request : requests) {
            Work work = save(request.toEntity(userId));
            works.add(work);
        }

        return works;
    }

    /**
     * 전체 경력 삭제 | sub |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(WORK_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Work> deleteAll(ObjectId userId, List<String> workIds) {
        List<Work> works = new ArrayList<>();

        for (String workId : workIds) {
            Work work = findOneById(workId);
            validateOwner(work, userId);

            works.add(work);
        }

        for (Work work: works)
            delete(work);

        return works;
    }

    /**
     * 경력 처리전 전체 검증 | sub |
     * 400(ID_CONVERT_INVALID)
     * 404(WORK_NOT_FOUND)
     */
    public void validatePreAll(List<WorkUpdateReqDto> workUpdateReqDtos,
                               List<String> deleteWorkIds) {
        for (WorkUpdateReqDto request : workUpdateReqDtos)
            findOneById(request.getWorkId());

        for (String deleteWorkId : deleteWorkIds)
            findOneById(deleteWorkId);
    }

    /**
     * 식별자로 경력 단건 조회 |
     * 400(ID_CONVERT_INVALID)
     * 404(WORK_NOT_FOUND)
     */
    private Work findOneById(String workId) {
        ObjectId id = utilityProvider.toObjectId(workId);

        return workRepository.findByIdAndIsDeletedIsFalse(id)
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
     private void delete(Work work) {
        work.delete();

        save(work);
    }

    /**
     * 소유자 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateOwner(Work work, ObjectId userId) {
        if (!work.getUserId().toString().equals(userId.toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);
    }
}
