package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Field(name = "user_id")
    private String userId;

    @Field(name = "is_deleted")
    private Boolean isDeleted;

    @Builder(builderClassName = "ByEducationBuilder", builderMethodName = "ByEducationBuilder")
    public Education(String institutionName,
                     LocalDate startedDate,
                     LocalDate endedDate,
                     Boolean isCurrent) {
        this.institutionName = institutionName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this.isDeleted = false;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void update(String institutionName, LocalDate startedDate, LocalDate endedDate, Boolean isCurrent) {
        this.institutionName = institutionName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
    }
}
