package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Level;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import com.inuappcenter.gabojaitspring.profile.dto.req.SkillDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;

    /**
     * 기술 저장 |
     * 500(SERVER_ERROR)
     */
    public Skill saveSkill(ObjectId userId, SkillDefaultReqDto request, Level level) {

        try {
            return skillRepository.save(request.toEntity(userId, level));
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 식별자 기술 조회 |
     * 404(WORK_NOT_FOUND)
     */
    public Skill findOneSkill(String skillId) {

        return skillRepository.findById(new ObjectId(skillId))
                .orElseThrow(() -> {
                    throw new CustomException(SKILL_NOT_FOUND);
                });
    }

    /**
     * 기술 업데이트 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void updateSkill(Skill skill, SkillDefaultReqDto request, Level level) {

        try {
            skill.update(request.getSkillName(),
                    request.getIsExperienced(),
                    level);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 기술 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void deleteSkill(Skill skill) {

        try {
            skill.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
