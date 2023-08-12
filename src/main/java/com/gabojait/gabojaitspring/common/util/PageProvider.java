package com.gabojait.gabojaitspring.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PageProvider {

    /**
     * 페이지 검증
     */
    public Pageable validatePageable(Integer pageSize, int defaultPageSize) {
        if (pageSize == null || pageSize <= 0)
            pageSize = defaultPageSize;

        return (Pageable) PageRequest.of(0, pageSize);
    }

    /**
     * 페이지 시작점 검증
     */
    public long validatePageFrom(long pageFrom) {
        return pageFrom == 0 ? Long.MAX_VALUE : pageFrom;
    }

    /**
     * 페이지 수 검증
     */
    public long validateCount(Long count) {
        return count == null ? 0 : count;
    }
}
