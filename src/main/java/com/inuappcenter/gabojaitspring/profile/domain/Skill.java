package com.inuappcenter.gabojaitspring.profile.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill {

    @Field(name = "skill_name")
    private String skillName;

    private Integer level;

    @Field(name = "is_deleted")
    private Boolean isDeleted;
}
