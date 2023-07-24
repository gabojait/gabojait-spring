package com.gabojait.gabojaitspring.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value = "* 페이지 데이터 응답")
public class PageDataDto<T>  {

    private T data;
    private Long size;

    @Builder
    public PageDataDto(final T data, final long size) {
        this.data = data;
        this.size = size;
    }
}
