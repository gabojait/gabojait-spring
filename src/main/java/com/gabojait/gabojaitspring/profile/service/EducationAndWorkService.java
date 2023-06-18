package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EducationAndWorkService {

    private final EducationService educationService;
    private final WorkService workService;

    /**
     * 학력과 경력 생성, 수정, 및 삭제 |
     * 404(EDUCATION_NOT_FOUND / WORK_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void createUpdateDelete(User user, EducationAndWorkDefaultReqDto request) {
        for(EducationCreateReqDto createReq : request.getCreateEducations())
            educationService.create(user, createReq);
        for(EducationUpdateReqDto updateReq : request.getUpdateEducations())
            educationService.update(user, updateReq);
        for(Long deleteReqId : request.getDeleteEducationIds())
            educationService.delete(user, deleteReqId);

        for(WorkCreateReqDto createReq : request.getCreateWorks())
            workService.create(user, createReq);
        for(WorkUpdateReqDto updateReq : request.getUpdateWorks())
            workService.update(user, updateReq);
        for(Long deleteReqId : request.getDeleteWorkIds())
            workService.delete(user, deleteReqId);
    }
}
