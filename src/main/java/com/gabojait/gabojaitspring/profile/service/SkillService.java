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
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(SKILL_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Skill> updateAll(ObjectId userId, List<SkillUpdateReqDto> requests) {
        List<Skill> skills = new ArrayList<>();

        for (SkillUpdateReqDto request : requests) {
            Skill skill = findOneById(request.getSkillId());
            validateOwner(skill, userId);

            skills.add(skill);
        }

        for (int i = 0; i < skills.size(); i++) {
            skills.get(i).update(requests.get(i).getSkillName(),
                    requests.get(i).getIsExperienced(),
                    Level.fromString(requests.get(i).getLevel()));

            save(skills.get(i));
        }

        return skills;
    }

    /**
     * 계정 삭제 전 전체 삭제 | main |
     * 500(SERVER_ERROR)
     */
    public void deleteAllPreDeactivation(List<Skill> skills) {
        for (Skill skill : skills)
            delete(skill);
    }

    /**
     * 전체 기술 생성 | sub |
     * 500(SERVER_ERROR)
     */
    public List<Skill> createAll(ObjectId userId, List<SkillCreateReqDto> requests) {
        List<Skill> skills = new ArrayList<>();

        for (SkillCreateReqDto request : requests) {
            Skill skill = save(request.toEntity(userId));
            skills.add(skill);
        }

        return skills;
    }

    /**
     * 전체 기술 삭제 | sub |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(SKILL_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Skill> deleteAll(ObjectId userId, List<String> skillIds) {
        List<Skill> skills = new ArrayList<>();

        for (String skillId : skillIds) {
            Skill skill = findOneById(skillId);
            validateOwner(skill, userId);

            skills.add(skill);
        }

        for (Skill skill: skills)
            delete(skill);

        return skills;
    }

    /**
     * 기술 처리전 전체 검증 | sub |
     * 400(ID_CONVERT_INVALID)
     * 404(SKILL_NOT_FOUND)
     */
    public void validatePreAll(List<SkillUpdateReqDto> skillUpdateReqDtos,
                               List<String> deleteSkillIds) {
        for (SkillUpdateReqDto request : skillUpdateReqDtos)
            findOneById(request.getSkillId());

        for (String deleteSkillId : deleteSkillIds)
            findOneById(deleteSkillId);
    }

    /**
     * 식별자로 기술 단건 조회 |
     * 400(ID_CONVERT_INVALID)
     * 404(SKILL_NOT_FOUND)
     */
    private Skill findOneById(String skillId) {
        ObjectId id = utilityProvider.toObjectId(skillId);

        return skillRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> {
                    throw new CustomException(SKILL_NOT_FOUND);
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
    private void delete(Skill skill) {
        skill.delete();

        save(skill);
    }

    /**
     * 소유자 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateOwner(Skill skill, ObjectId userId) {
        if (!skill.getUserId().toString().equals(userId.toString()))
            throw new CustomException(REQUEST_FORBIDDEN);
    }
}
