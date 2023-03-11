package com.inuappcenter.gabojaitspring.profile.domain.type;

import lombok.Getter;

public enum TeamMemberStatus {

    LEADER('L'),
    MEMBER('M'),
    NULL('N');

    @Getter
    Character type;

    TeamMemberStatus(Character type) {
        this.type = type;
    }

    public static TeamMemberStatus toEnum(Character type) {

        switch (type) {
            case 'L':
                return TeamMemberStatus.LEADER;
            case 'M':
                return TeamMemberStatus.MEMBER;
            default:
                return TeamMemberStatus.NULL;
        }
    }
}
