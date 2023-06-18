package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.dto.req.SkillCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.SkillUpdateReqDto;
import com.gabojait.gabojaitspring.profile.repository.SkillRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    /**
     * 기술 생성 |
     * 500(SERVER_ERROR)
     */
    public void create(User user, SkillCreateReqDto request) {
        Skill skill = request.toEntity(user);

        saveSkill(skill);
    }

    /**
     * 기술 업데이트 |
     * 404(SKILL_NOT_FOUND)
     */
    public void update(User user, SkillUpdateReqDto request) {
        Skill skill = findOneSkill(request.getSkillId(), user);

        skill.update(request.getSkillName(), request.getIsExperienced(), Level.fromString(request.getLevel()));
    }

    /**
     * 기술 삭제 |
     * 404(SKILL_NOT_FOUND)
     */
    public void delete(User user, Long skillId) {
        Skill skill = findOneSkill(skillId, user);

        softDeleteSkill(skill);
    }

    /**
     * 기술 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveSkill(Skill skill) {
        try {
            skillRepository.save(skill);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 기술 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    private void softDeleteSkill(Skill skill) {
        skill.delete();
    }

    /**
     * 기술 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    public void hardDelete(Skill skill) {
        try {
            skillRepository.delete(skill);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 식별자와 회원으로 기술 단건 조회 |
     * 404(SKILL_NOT_FOUND)
     */
    private Skill findOneSkill(Long skillId, User user) {
        return skillRepository.findByIdAndUserAndIsDeletedIsFalse(skillId, user)
                .orElseThrow(() -> {
                    throw new CustomException(SKILL_NOT_FOUND);
                });
    }
}
