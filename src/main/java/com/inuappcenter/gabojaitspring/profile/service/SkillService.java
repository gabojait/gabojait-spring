package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Level;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import com.inuappcenter.gabojaitspring.profile.dto.req.SkillDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.repository.SkillRepository;
import com.inuappcenter.gabojaitspring.user.domain.User;
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
    public Skill save(Skill skill) {

        try {
            return skillRepository.save(skill);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 식별자 기술 조회 |
     * 404(WORK_NOT_FOUND)
     */
    public Skill findOne(String skillId) {

        return skillRepository.findById(new ObjectId(skillId))
                .orElseThrow(() -> {
                    throw new CustomException(SKILL_NOT_FOUND);
                });
    }

    /**
     * 권한 검증 |
     * 403(ROLE_NOT_ALLOWED)
     */
    public void validateOwner(Skill skill, User user) {

        if (!user.getSkills().contains(skill))
            throw new CustomException(ROLE_NOT_ALLOWED);
    }

    /**
     * 기술 업데이트 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void update(Skill skill, SkillDefaultReqDto request, Level level) {

        try {
            skill.update(request.getSkillName(),
                    request.getIsExperienced(),
                    level);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(skill);
    }

    /**
     * 기술 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void delete(Skill skill) {

        try {
            skill.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(skill);
    }
}
