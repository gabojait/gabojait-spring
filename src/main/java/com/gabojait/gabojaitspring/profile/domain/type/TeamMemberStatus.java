package com.gabojait.gabojaitspring.profile.domain.type;

import lombok.Getter;

@Getter
public enum TeamMemberStatus {

    LEADER('L'),
    MEMBER('M'),
    NULL('N');

    Character type;

    TeamMemberStatus(Character type) {
        this.type = type;
    }

    public static TeamMemberStatus toEnum(Character type) {
        TeamMemberStatus teamMemberStatus;

        switch (type) {
            case 'L':
                teamMemberStatus = TeamMemberStatus.LEADER;
                break;
            case 'M':
                teamMemberStatus = TeamMemberStatus.MEMBER;
                break;
            default:
                teamMemberStatus = TeamMemberStatus.NULL;
                break;
        }

        return teamMemberStatus;
    }
}
