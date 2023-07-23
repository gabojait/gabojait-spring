package com.gabojait.gabojaitspring.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value = "* 페이지 데이터 응답")
public class PageDataDto<T>  {

    private T data;
    private Integer size;

    @Builder
    public PageDataDto(final T data, final Integer size) {
        this.data = data;
        this.size = size;
    }
}
