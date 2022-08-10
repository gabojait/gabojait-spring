package com.inuappcenter.gabojaitspring.common;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
public class BaseTimeEntity extends BaseEntity{
    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
