package com.inuappcenter.gabojaitspring.common;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
public class BaseTimeEntity extends BaseEntity{
    @LastModifiedDate
    @Field(name = "modified_date")
    private LocalDateTime modifiedDate;
}
