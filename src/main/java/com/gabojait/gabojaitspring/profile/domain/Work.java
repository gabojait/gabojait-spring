package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@ToString
@Document(collection = "work")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Work extends BaseTimeEntity {

    @Field(name = "user_id")
    private ObjectId userId;

    @Field(name = "corporation_name")
    private String corporationName;

    @Field(name = "started_date")
    private LocalDate startedDate;

    @Field(name = "ended_date")
    private LocalDate endedDate;

    @Field(name = "is_current")
    private Boolean isCurrent;

    @Field(name = "work_description")
    private String workDescription;

    @Builder
    public Work(ObjectId userId,
                String corporationName,
                LocalDate startedDate,
                LocalDate endedDate,
                boolean isCurrent,
                String workDescription) {
        this.userId = userId;
        this.corporationName = corporationName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this.workDescription = workDescription;
    }

    public void update(String corporationName,
                       LocalDate startedDate,
                       LocalDate endedDate,
                       boolean isCurrent,
                       String workDescription) {
        this.corporationName = corporationName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this. workDescription = workDescription;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
