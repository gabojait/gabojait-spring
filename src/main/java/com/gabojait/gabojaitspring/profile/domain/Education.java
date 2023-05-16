package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
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

    @Field(name = "user_id")
    private ObjectId userId;

    @Field(name = "institution_name")
    private String institutionName;

    @Field(name = "started_date")
    private LocalDate startedDate;

    @Field(name = "ended_date")
    private LocalDate endedDate;

    @Field(name = "is_current")
    private Boolean isCurrent;

    @Builder
    public Education(ObjectId userId,
                     String institutionName,
                     LocalDate startedDate,
                     LocalDate endedDate,
                     boolean isCurrent) {
        this.userId = userId;
        this.institutionName = institutionName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this.isDeleted = false;
    }

    public void update(String institutionName, LocalDate startedDate, LocalDate endedDate, boolean isCurrent) {
        this.institutionName = institutionName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
