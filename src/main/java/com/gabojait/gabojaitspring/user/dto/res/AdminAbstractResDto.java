package com.gabojait.gabojaitspring.user.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.user.domain.Admin;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "관리자 요약 응답")
public class AdminAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "관리자 식별자")
    private Long adminId;

    @ApiModelProperty(position = 2, required = true, value = "아이디")
    private String username;

    @ApiModelProperty(position = 3, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 4, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public AdminAbstractResDto(Admin admin) {
        this.adminId = admin.getId();
        this.username = admin.getUsername();
        this.createdAt = admin.getCreatedAt();
        this.updatedAt = admin.getUpdatedAt();
    }
}
