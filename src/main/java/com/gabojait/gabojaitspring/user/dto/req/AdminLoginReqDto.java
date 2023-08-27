package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({AdminLoginReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "관리자 로그인 요청")
public class AdminLoginReqDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "admin")
    @NotBlank(message = "아이디는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String password;

    @Builder
    private AdminLoginReqDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
