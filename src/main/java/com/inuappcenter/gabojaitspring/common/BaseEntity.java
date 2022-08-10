package com.inuappcenter.gabojaitspring.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
public class BaseEntity {
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    protected String schemaVersion = "1.0";

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }
}
