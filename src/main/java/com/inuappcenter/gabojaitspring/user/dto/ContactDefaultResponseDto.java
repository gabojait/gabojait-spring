package com.inuappcenter.gabojaitspring.user.dto;

import com.inuappcenter.gabojaitspring.user.domain.Contact;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "Contact 응답")
public class ContactDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "연락처 식별자")
    private String contactId;

    @ApiModelProperty(position = 2, required = true, value = "이메일")
    private String email;

    @ApiModelProperty(position = 3, required = true, value = "인증여부")
    private Boolean isVerified;

    @ApiModelProperty(position = 4, required = true, value = "가입여부")
    private Boolean isRegistered;

    @ApiModelProperty(position = 5, required = true, value = "스키마버전")
    private String schemaVersion;

    public ContactDefaultResponseDto(Contact contact) {
        this.contactId = contact.getId().toString();
        this.email = contact.getEmail();
        this.isVerified = contact.getIsVerified();
        this.isRegistered = contact.getIsRegistered();
        this.schemaVersion = contact.getSchemaVersion();
    }
}
