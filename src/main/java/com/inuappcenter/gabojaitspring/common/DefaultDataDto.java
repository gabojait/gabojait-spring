package com.inuappcenter.gabojaitspring.common;

import lombok.Builder;
import lombok.Data;

@Data
public class DefaultDataDto<T> {

    private T data;
    private Integer size;

    @Builder(builderClassName = "NoDataBuilder", builderMethodName = "NoDataBuilder")
    public DefaultDataDto() {
        this.data = null;
        this.size = 0;
    }

    @Builder(builderClassName = "SingleDataBuilder", builderMethodName = "SingleDataBuilder")
    public DefaultDataDto(final T data) {
        this.data = data;
        this.size = 0;
    }

    @Builder(builderClassName = "MultiDataBuilder", builderMethodName = "MultiDataBuilder")
    public DefaultDataDto(final T data, final Integer size) {
        this.data = data;
        this.size = size;
    }
}
