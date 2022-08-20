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
@GroupSequence({UserDuplicateRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Pattern.class})
@ApiModel(value = "User 중복 여부 확인 요청")
public class UserDuplicateRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "아이디", example = "username")
    @NotBlank(message = "아이디를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
            message = "아이디 형식은 영문과 숫자의 조합만 가능합니다",
            groups = ValidationSequence.Pattern.class)
    private String username;
}
