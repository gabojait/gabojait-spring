package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.domain.Level;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import com.inuappcenter.gabojaitspring.profile.dto.SkillSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.SkillUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    /**
     * 기술 생성 |
     * 기술 생성 절차를 밟아서 정보를 저장한다. |
     * 500: 기술 정보 저장 중 서버 에러
     */
    public Skill save(SkillSaveRequestDto request, Profile profile) {
        log.info("INITIALIZE | SkillService | save | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        Level level = validateLevel(request.getLevel());

        Skill skill = request.toEntity(profile.getId(), level);

        try {
            skill = skillRepository.save(skill);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | SkillService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + skill.getId());
        return skill;
    }

    /**
     * 레벨 검증 |
     * 레벨이 1, 2, 또는 3으로 되어 있는지 확인한다. |
     * 400: 올바르지 않을 포맷 에러
     */
    private Level validateLevel(Integer level) {
        log.info("PROGRESS | SkillService | validateLevel | " + level);

        if (level == Level.LOW.getType().intValue()) {
            return Level.LOW;
        } else if (level == Level.MID.getType().intValue()) {
            return Level.MID;
        } else if (level == Level.HIGH.getType().intValue()) {
            return Level.HIGH;
        } else{
            throw new CustomException(LEVEL_INCORRECT_TYPE);
        }
    }

    /**
     * 기술 업데이트 |
     * 기술 정보를 조회하여 업데이트한다. |
     * 500: 기술 정보 저장 중 서버 에러
     */
    public void update(Profile profile, SkillUpdateRequestDto request) {
        log.info("INITIALIZE | SkillService | update | " + profile.getId() + " | " + request.getSkillId());
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId skillId = new ObjectId(request.getSkillId());
        Skill skill = findOne(profile, skillId);
        Level level = validateLevel(request.getLevel());


        skill.updateSkill(request.getSkillName(),
                request.getIsExperienced(),
                level);

        try {
            skill = skillRepository.save(skill);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | SkillService | update | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + skill.getId());
    }

    /**
     * 기술 단건 조회 |
     * 기술 정보가 프로필 정보에 있는지 확인하고 반환한다. |
     * 404: 존재하지 않은 기술 정보 에러
     */
    private Skill findOne(Profile profile, ObjectId skillId) {
        log.info("PROGRESS | SkillService | findOne | " + profile.getId() + " | " + skillId);

        for (Skill skill : profile.getSkills()) {
            if (skill.getId().equals(skillId)) {
                return skill;
            }
        }

        throw new CustomException(NON_EXISTING_SKILL);
    }

    /**
     * 기술 제거 |
     * 기술 정보에 제거 표시를 한 후 기술을 반환한다. |
     * 500: 기술 정보 저장 중 서버 에러
     */
    public Skill delete(Profile profile, String skillId) {
        log.info("INITIALIZE | SkillService | delete | " + profile.getId() + " | " + skillId);
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId id = new ObjectId(skillId);
        Skill skill = findOne(profile, id);

        skill.deleteSkill();

        try {
            skillRepository.save(skill);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | SkillService | delete | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                skill.getId() + " | " + skill.getIsDeleted());
        return skill;
    }
}
