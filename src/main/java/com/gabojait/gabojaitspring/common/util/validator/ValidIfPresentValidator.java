package com.gabojait.gabojaitspring.common.util.validator;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.dto.req.TeamMemberRecruitCntReqDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidIfPresentValidator implements ConstraintValidator<ValidIfPresent, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        if (value instanceof TeamMemberRecruitCntReqDto) {
            if (isInvalidTeamRecruitCntRequest(value))
                return false;
        }

        return true;
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
