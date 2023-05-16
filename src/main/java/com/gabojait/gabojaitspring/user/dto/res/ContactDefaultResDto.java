package com.gabojait.gabojaitspring.user.dto.res;

import com.gabojait.gabojaitspring.user.domain.Contact;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "연락처 기본 응답")
public class ContactDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "연락처 식별자")
    private String contactId;

    @ApiModelProperty(position = 2, required = true, value = "이메일")
    private String email;

    @ApiModelProperty(position = 3, required = true, value = "인증 여부")
    private Boolean isVerified;

    @ApiModelProperty(position = 4, required = true, value = "가입 여부")
    private Boolean isRegistered;

    @ApiModelProperty(position = 5, required = true, value = "스키마 버전")
    private String schemaVersion;

    public ContactDefaultResDto(Contact contact) {
        this.contactId = contact.getId().toString();
        this.email = contact.getEmail();
        this.isVerified = contact.getIsVerified();
        this.isRegistered = contact.getIsRegistered();
        this.schemaVersion = contact.getSchemaVersion();
    }
}
