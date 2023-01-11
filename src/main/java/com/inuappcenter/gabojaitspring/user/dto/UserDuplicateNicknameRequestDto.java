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
@GroupSequence({UserDuplicateNicknameRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Pattern.class})
@ApiModel(value = "User 닉네임 중복여부 확인 요청")
public class UserDuplicateNicknameRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "닉네임", example = "닉네임",
            allowableValues = "Restriction: [NotNull > Size > Pattern]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글만 가능합니다.", groups = ValidationSequence.Pattern.class)
    private String nickname;
}
