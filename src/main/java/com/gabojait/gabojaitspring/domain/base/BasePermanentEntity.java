package com.gabojait.gabojaitspring.domain.base;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
public abstract class BasePermanentEntity extends BaseEntity {

    @Column(nullable = false)
    protected Boolean isDeleted;
}
