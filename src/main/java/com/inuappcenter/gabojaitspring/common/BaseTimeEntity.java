package com.inuappcenter.gabojaitspring.common;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Getter
public abstract class BaseTimeEntity {

    @MongoId
    private ObjectId id;

    @CreatedDate
    @Field(name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Field(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Field(name = "is_deleted")
    protected Boolean isDeleted;

    @Field(name = "schema_version")
    protected String schemaVersion = "1.0";
}
