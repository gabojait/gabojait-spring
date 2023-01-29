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

    private String description;

    @Builder
    public Work(ObjectId userId,
                String corporationName,
                LocalDate startedDate,
                LocalDate endedDate,
                boolean isCurrent,
                String description) {
        this.userId = userId;
        this.corporationName = corporationName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this.description = description;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void update(String corporationName,
                       LocalDate startedDate,
                       LocalDate endedDate,
                       boolean isCurrent,
                       String description) {
        this.corporationName = corporationName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
        this. description = description;
    }
}
