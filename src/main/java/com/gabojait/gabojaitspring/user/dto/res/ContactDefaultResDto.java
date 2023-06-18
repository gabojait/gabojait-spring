package com.gabojait.gabojaitspring.user.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.user.domain.Contact;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "연락처 기본 응답")
public class ContactDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "연락처 식별자")
    private Long contactId;

    @ApiModelProperty(position = 2, required = true, value = "이메일")
    private String email;

    @ApiModelProperty(position = 3, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 4, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public ContactDefaultResDto(Contact contact) {
        this.contactId = contact.getId();
        this.email = contact.getEmail();
        this.createdAt = contact.getCreatedAt();
        this.updatedAt = contact.getUpdatedAt();
    }
}
