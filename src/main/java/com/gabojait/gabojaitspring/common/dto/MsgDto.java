package com.gabojait.gabojaitspring.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class MsgDto {

    private String msgKr;
    private String msgEn;

    @Builder
    public MsgDto(final String msgKr, final String msgEn) {
        this.msgKr = msgKr;
        this.msgEn = msgEn;
    }
}
