package com.inuappcenter.gabojaitspring.user.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({UserVerifyPasswordReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Pattern.class
})
@ApiModel(value = "User 비밀번호 검증 요청")
public class UserVerifyPasswordReqDto {

    @ApiModelProperty(position = 1, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호를 입력해주세요", groups = ValidationSequence.NotBlank.class)
    private String password;
}
