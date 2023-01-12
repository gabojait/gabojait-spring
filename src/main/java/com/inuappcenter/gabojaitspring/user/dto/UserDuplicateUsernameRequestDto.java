package com.inuappcenter.gabojaitspring.user.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({UserDuplicateUsernameRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Pattern.class})
@ApiModel(value = "User 아이디 중복여부 확인 요청")
public class UserDuplicateUsernameRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "username")
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.")
    @Pattern(regexp = "^(?=.*[A-z])(?=.*\\d)[A-z\\d]+$", message = "아이디는 영문과 숫자의 조합의 형식만 가능합니다.",
            groups = ValidationSequence.Pattern.class)
    private String username;
}
