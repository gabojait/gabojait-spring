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
@Document(collection = "work")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Work extends BaseTimeEntity {

    @Field(name = "corporation_name")
    private String corporationName;

    private String about;

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

    @Builder(builderClassName = "ByWorkBuilder", builderMethodName = "ByWorkBuilder")
    public Work(String corporationName,
                String about,
                LocalDate startedDate,
                LocalDate endedDate,
                Boolean isCurrent) {
        this.corporationName = corporationName;
        this.about = about;
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

    public void update(String corporationName,
                       String about,
                       LocalDate startedDate,
                       LocalDate endedDate,
                       Boolean isCurrent) {
        this.corporationName = corporationName;
        this.about = about;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
    }
}
