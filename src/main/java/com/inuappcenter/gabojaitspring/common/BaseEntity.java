package com.inuappcenter.gabojaitspring.common;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
public abstract class BaseEntity {
    @Id
    private String id;

    @CreatedDate
    @Field(name = "created_date")
    private LocalDateTime createdDate;

    @Field(name = "schema_version")
    protected String schemaVersion = "1.0";

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }
}
