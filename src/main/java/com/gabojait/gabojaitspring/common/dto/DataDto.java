package com.gabojait.gabojaitspring.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value = "* 데이터 응답")
public class DataDto<T> {

    private T data;
    private Integer size;

    @Builder(builderMethodName = "noDataBuilder", builderClassName = "noDataBuilder")
    public DataDto() {
        this.data = null;
        this.size = 0;
    }

    @Builder(builderMethodName = "singleDataBuilder", builderClassName = "singleDataBuilder")
    public DataDto(final T data) {
        this.data = data;
        this.size = 1;
    }

    @Builder(builderMethodName = "multiDataBuilder", builderClassName = "multiDataBuilder")
    public DataDto(final T data, final Integer size) {
        this.data = data;
        this.size = size;
    }
}
