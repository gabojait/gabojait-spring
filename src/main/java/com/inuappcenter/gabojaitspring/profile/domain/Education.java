package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@Document(collection = "education")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education extends BaseTimeEntity {

    @Field(name = "institution_name")
    private String institutionName;

    @Field(name = "started_date")
    private LocalDate startedDate;

    @Field(name = "ended_date")
    private LocalDate endedDate;

    @Field(name = "is_current")
    private Boolean isCurrent;

    @Field(name = "profile_id")
    private ObjectId profileId;

    @Builder
    public Education(String institutionName,
                     LocalDate startedDate,
                     LocalDate endedDate,
                     Boolean isCurrent,
                     ObjectId profileId) {
        this.institutionName = institutionName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this.profileId = profileId;
        this.isDeleted = false;
    }

    public void deleteEducation() {
        this.isCurrent = true;
    }

    public void updateEducation(String institutionName, LocalDate startedDate, LocalDate endedDate, Boolean isCurrent) {
        this.institutionName = institutionName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
    }
}
