package com.gabojait.gabojaitspring.common.util.validator;

import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.team.dto.req.TeamMemberRecruitCntReqDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidIfPresentValidator implements ConstraintValidator<ValidIfPresent, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        if (value instanceof EducationCreateReqDto) {
            if (isInvalidEducationRequest(value))
                return false;

            if (value instanceof EducationUpdateReqDto) {
                EducationUpdateReqDto dto = (EducationUpdateReqDto) value;
                if (isInvalidId(dto.getEducationId()))
                    return false;
            }
        } else if (value instanceof PortfolioLinkCreateReqDto) {
            if (isInvalidPortfolioLinkRequest(value))
                return false;

            if (value instanceof PortfolioLinkUpdateReqDto) {
                PortfolioLinkUpdateReqDto dto = (PortfolioLinkUpdateReqDto) value;
                if (isInvalidId(dto.getPortfolioId()))
                    return false;
            }
        } else if (value instanceof SkillCreateReqDto) {
            if (isInvalidSkillRequest(value))
                return false;

            if (value instanceof SkillUpdateReqDto) {
                SkillUpdateReqDto dto = (SkillUpdateReqDto) value;
                if (isInvalidId(dto.getSkillId()))
                    return false;
            }
        } else if (value instanceof WorkCreateReqDto) {
            if (isInvalidWorkRequest(value))
                return false;

            if (value instanceof WorkUpdateReqDto) {
                WorkUpdateReqDto dto = (WorkUpdateReqDto) value;
                if (isInvalidId(dto.getWorkId()))
                    return false;
            }
        } else if (value instanceof TeamMemberRecruitCntReqDto) {
            if (isInvalidTeamRecruitCntRequest(value))
                return false;
        }

        return true;
    }

    private boolean isInvalidId(Long id) {
        return id == null || id > 0;
    }

    private boolean isInvalidSkillRequest(Object value) {
        SkillCreateReqDto dto = (SkillCreateReqDto) value;

        // Blank
        if (dto.getSkillName().isBlank())
            return true;
        if (dto.getIsExperienced() == null)
            return true;
        if (dto.getLevel().isBlank())
            return true;

        // Size
        if (dto.getSkillName().length() > 20 || dto.getSkillName().length() < 1)
            return true;

        // Format
        if (!dto.getLevel().equals(Level.HIGH.name().toLowerCase()) &&
                !dto.getLevel().equals(Level.MID.name().toLowerCase()) &&
                !dto.getLevel().equals(Level.LOW.name().toLowerCase()))
            return true;

        return false;
    }

    private boolean isInvalidEducationRequest(Object value) {
        EducationCreateReqDto dto = (EducationCreateReqDto) value;

        // Blank
        if (dto.getInstitutionName().isBlank())
            return true;
        if (dto.getStartedAt() == null)
            return true;
        if (dto.getIsCurrent() == null)
            return true;
        if (dto.getIsCurrent() == false && dto.getEndedAt() == null)
            return true;

        // Size
        if (dto.getInstitutionName().length() < 3 || dto.getInstitutionName().length() > 20)
            return true;

        return false;
    }

    private boolean isInvalidPortfolioLinkRequest(Object value) {
        PortfolioLinkCreateReqDto dto = (PortfolioLinkCreateReqDto) value;

        // Blank
        if (dto.getPortfolioName().isBlank())
            return true;
        if (dto.getPortfolioUrl().isBlank())
            return true;

        // Size
        if (dto.getPortfolioName().length() < 1 || dto.getPortfolioName().length() > 10)
            return true;
        if (dto.getPortfolioUrl().length() < 1 || dto.getPortfolioUrl().length() > 1000)
            return true;

        return false;
    }

    private boolean isInvalidWorkRequest(Object value) {
        WorkCreateReqDto dto = (WorkCreateReqDto) value;

        // Blank
        if (dto.getCorporationName().isBlank())
            return true;
        if (dto.getStartedAt() == null)
            return true;
        if (dto.getIsCurrent() == null)
            return true;
        if (dto.getIsCurrent() == false && dto.getEndedAt() == null)
            return true;

        // Size
        if (dto.getCorporationName().length() < 1 || dto.getCorporationName().length() > 20)
            return true;
        if (dto.getWorkDescription().length() > 100)
            return true;

        return false;
    }

    private boolean isInvalidTeamRecruitCntRequest(Object value) {
        TeamMemberRecruitCntReqDto dto = (TeamMemberRecruitCntReqDto) value;

        // Blank
        if (dto.getPosition().isBlank())
            return true;
        if (dto.getTotalRecruitCnt() == null)
            return true;

        // Format
        if (dto.getTotalRecruitCnt() <= 0)
            return true;
        if (!dto.getPosition().equals(Position.DESIGNER.name()) &&
                !dto.getPosition().equals(Position.BACKEND.name()) &&
                !dto.getPosition().equals(Position.FRONTEND.name()) &&
                !dto.getPosition().equals(Position.MANAGER.name()))
            return true;

        return false;
    }
}
