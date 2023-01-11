package com.inuappcenter.gabojaitspring.user.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({UserFindPasswordDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Email.class})
@ApiModel(value = "User 비밀번호 찾기 요청")
public class UserFindPasswordDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "username",
            allowableValues = "Restriction: [NotBlank]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "이메일", example = "email@domain.com",
            allowableValues = "Format: [XXXX@XXXXX.XXX], Restriction: [NotBlank > Email]")
    @NotBlank(message = "이메일을 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Email(message = "올바른 이메일 형식이 아닙니다.", groups = ValidationSequence.Email.class)
    private String email;
}