package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({UserLoginReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "회원 로그인 요청")
public class UserLoginReqDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "tester")
    @NotBlank(message = "아이디는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String password;

    @ApiModelProperty(position = 3, value = "FCM 토큰")
    private String fcmToken;
}
