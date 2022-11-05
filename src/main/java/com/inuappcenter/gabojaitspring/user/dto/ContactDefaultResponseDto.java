package com.inuappcenter.gabojaitspring.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "Contact 응답")
public class ContactDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "식별자")
    private String contactId;

    @ApiModelProperty(position = 2, required = true, value = "이메일")
    private String email;

    @ApiModelProperty(position = 3, required = true, value = "인증번호")
    private String verificationCode;

    @ApiModelProperty(position = 4, required = true, value = "인증여부")
    private Boolean isVerified;

    @ApiModelProperty(position = 5, required = true, value = "가입여부")
    private Boolean isRegistered;

    @ApiModelProperty(position = 6, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    @ApiModelProperty(position = 7, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime modifiedDate;

    @ApiModelProperty(position = 8, required = true, value = "스키마버전")
    private String schemaVersion;

    public ContactDefaultResponseDto(Contact contact) {
        this.contactId = contact.getId();
        this.email = contact.getEmail();
        this.verificationCode = contact.getVerificationCode();
        this.isVerified = contact.getIsVerified();
        this.isRegistered = contact.getIsRegistered();
        this.createdDate = contact.getCreatedDate();
        this.modifiedDate = contact.getModifiedDate();
        this.schemaVersion = contact.getSchemaVersion();
    }
}
