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
@Document(collection = "work")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Work extends BaseTimeEntity {

    @Field(name = "corporation_name")
    private String corporationName;

    @Field(name = "started_date")
    private LocalDate startedDate;

    @Field(name = "ended_date")
    private LocalDate endedDate;

    @Field(name = "is_current")
    private Boolean isCurrent;

    @Field(name = "profile_id")
    private ObjectId profileId;

    private String description;

    @Builder
    public Work(String corporationName,
                LocalDate startedDate,
                LocalDate endedDate,
                Boolean isCurrent,
                ObjectId profileId,
                String description) {
        this.corporationName = corporationName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this.profileId = profileId;
        this.description = description;
        this.isDeleted = false;
    }

    public void deleteWork() {
        this.isDeleted = true;
    }

    public void updateWork(String corporationName,
                           LocalDate startedDate,
                           LocalDate endedDate,
                           Boolean isCurrent,
                           String description) {
        this.corporationName = corporationName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this.description = description;
    }
}
