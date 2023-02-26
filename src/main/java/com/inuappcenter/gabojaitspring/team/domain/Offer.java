package com.inuappcenter.gabojaitspring.team.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

public class Offer extends BaseTimeEntity {

    @Field(name = "is_accepted")
    private Boolean isAccepted;

    @Field(name = "is_by_applicant")
    private Boolean isByApplicant;

    private User applicant;
    private Team team;

    @Builder()
    public Offer(User applicant, Team team, Boolean isByApplicant) {
        this.applicant = applicant;
        this.team = team;
        this.isByApplicant = isByApplicant;
        this.isDeleted = false;
    }

    public void accept() {
        this.isAccepted = true;
        this.isDeleted = true;
    }

    public void decline() {
        this.isAccepted = false;
        this.isDeleted = true;
    }
}
