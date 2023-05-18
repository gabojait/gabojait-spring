package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@ApiModel(value = "회원 닉네임 업데이트 요청")
@NoArgsConstructor
@GroupSequence({UserNicknameUpdateReqDto.class, ValidationSequence.Blank.class, ValidationSequence.Size.class})
public class UserNicknameUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "닉네임", example = "김가볼까잇")
    @NotBlank(message = "닉네임은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String nickname;
}
