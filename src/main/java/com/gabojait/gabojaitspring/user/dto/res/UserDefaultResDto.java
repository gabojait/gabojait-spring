package com.gabojait.gabojaitspring.user.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ApiModel(value = "회원 기본 응답")
public class UserDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private String userId;

    @ApiModelProperty(position = 2, required = true, value = "아이디")
    private String username;

    @ApiModelProperty(position = 3, required = true, value = "실명")
    private String legalName;

    @ApiModelProperty(position = 4, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 5, required = true, value = "성별")
    private String gender;

    @ApiModelProperty(position = 6, required = true, value = "생년월일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @ApiModelProperty(position = 7, required = true, value = "연락처")
    private ContactDefaultResDto contact;

    @ApiModelProperty(position = 8, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @ApiModelProperty(position = 9, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @ApiModelProperty(position = 10, required = true, value = "스키마 버전")
    private String schemaVersion;

    public UserDefaultResDto(User user) {
        this.userId = user.getId().toString();
        this.username = user.getUsername();
        this.legalName = user.getLegalName();
        this.nickname = user.getNickname();
        this.gender = Gender.fromChar(user.getGender()).name();
        this.birthdate = user.getBirthdate();
        this.contact = new ContactDefaultResDto(user.getContact());
        this.createdDate = user.getCreatedDate();
        this.modifiedDate = user.getModifiedDate();
        this.schemaVersion = user.getSchemaVersion();
    }
}
