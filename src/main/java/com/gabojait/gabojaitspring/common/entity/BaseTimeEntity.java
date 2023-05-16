package com.gabojait.gabojaitspring.common.entity;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Getter
public abstract class BaseTimeEntity {

    @MongoId
    private ObjectId id;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    protected LocalDateTime modifiedDate;

    protected Boolean isDeleted;

    private String schemaVersion = "1.0";
}
