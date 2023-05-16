package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.dto.req.SkillCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.SkillUpdateReqDto;
import com.gabojait.gabojaitspring.profile.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UtilityProvider utilityProvider;

    /**
     * 전체 기술 업데이트 | main |
     * 403(REQUEST_FORBIDDEN)
     * 404(SKILL_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void updateAll(ObjectId userId, List<SkillUpdateReqDto> requests) {
        List<Skill> skills = new ArrayList<>();
        for (SkillUpdateReqDto request: requests) {
            ObjectId id = utilityProvider.toObjectId(request.getSkillId());

            Skill skill = findOne(id);
            validateOwner(skill, userId);

            skills.add(skill);
        }

        for (int i = 0; i < skills.size(); i++) {
            skills.get(i).update(requests.get(i).getSkillName(),
                    requests.get(i).getIsExperienced(),
                    Level.fromString(requests.get(i).getLevel()));

            save(skills.get(i));
        }
    }

    /**
     * 전체 기술 생성 | sub |
     * 500(SERVER_ERROR)
     */
    public List<Skill> createAll(ObjectId userId, List<SkillCreateReqDto> requests) {
        List<Skill> skills = new ArrayList<>();
        for (SkillCreateReqDto request: requests) {
            Skill skill = save(request.toEntity(userId));
            skills.add(skill);
        }

        return skills;
    }

    /**
     * 전체 기술 삭제 | sub |
     * 403(REQUEST_FORBIDDEN)
     * 404(SKILL_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Skill> deleteAll(ObjectId userId, List<String> skillIds) {
        List<Skill> skills = new ArrayList<>();
        for (String skillId: skillIds) {
            ObjectId id = utilityProvider.toObjectId(skillId);

            Skill skill = findOne(id);
            validateOwner(skill, userId);

            skills.add(skill);
        }

        for (Skill skill: skills)
            softDelete(skill);

        return skills;
    }

    /**
     * 소유자 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateOwner(Skill skill, ObjectId userId) {
        if (!skill.getUserId().equals(userId))
            throw new CustomException(null, REQUEST_FORBIDDEN);
    }

    /**
     * 기술 단건 조회 |
     * 404(SKILL_NOT_FOUND)
     */
    private Skill findOne(ObjectId skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    throw new CustomException(null, SKILL_NOT_FOUND);
                });
    }

    /**
     * 기술 저장 |
     * 500(SERVER_ERROR)
     */
    public Skill save(Skill skill) {
        try {
            return skillRepository.save(skill);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 기술 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    public void softDelete(Skill skill) {
        skill.delete();

        save(skill);
    }

    /**
     * 기술 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    private void hardDelete(Skill skill) {
        try {
            skillRepository.delete(skill);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
