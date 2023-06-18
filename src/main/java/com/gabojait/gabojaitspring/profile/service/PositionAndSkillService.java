package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.dto.req.PositionAndSkillDefaultReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.SkillCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.SkillUpdateReqDto;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PositionAndSkillService {

    private final SkillService skillService;

    /**
     * 포지션 업데이트와 기술 생성, 수정, 및 삭제 |
     * 404(SKILL_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void createUpdateDelete(User user, PositionAndSkillDefaultReqDto request) {
        user.updatePosition(Position.fromString(request.getPosition()));

        for(SkillCreateReqDto createReq : request.getCreateSkills())
            skillService.create(user, createReq);
        for(SkillUpdateReqDto updateReq : request.getUpdateSkills())
            skillService.update(user, updateReq);
        for(Long deleteReqId : request.getDeleteSkillIds())
            skillService.delete(user, deleteReqId);
    }
}
