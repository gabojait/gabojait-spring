package com.gabojait.gabojaitspring.api.dto.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value = "* 페이지 데이터 응답")
public class PageData<T>  {

    private T data;
    private Long total;

    @Builder
    public PageData(final T data, final long total) {
        this.data = data;
        this.total = total;
    }
}
