package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "education")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education extends BaseTimeEntity {

    @Field(name = "institution_name")
    private String institutionName;

    @Field(name = "started_date")
    private String startedDate;

    @Field(name = "ended_date")
    private String endedDate;

    @Field(name = "is_current")
    private Boolean isCurrent;

    @Field(name = "is_deleted")
    private Boolean isDeleted;
}
