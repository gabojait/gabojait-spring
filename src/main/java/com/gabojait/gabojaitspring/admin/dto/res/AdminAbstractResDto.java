package com.gabojait.gabojaitspring.admin.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "관리자 요약 응답")
public class AdminAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private String userId;

    @ApiModelProperty(position = 2, required = true, value = "아이디")
    private String username;

    @ApiModelProperty(position = 3, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @ApiModelProperty(position = 4, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @ApiModelProperty(position = 5, required = true, value = "스키마 버전")
    private String schemaVersion;

    public AdminAbstractResDto(User user) {
        this.userId = user.getId().toString();
        this.username = user.getUsername();
        this.createdDate = user.getCreatedDate();
        this.modifiedDate = user.getModifiedDate();
        this.schemaVersion = user.getSchemaVersion();
    }
}
