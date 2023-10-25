package com.gabojait.gabojaitspring.api.dto.user.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({UserNicknameUpdateRequest.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "회원 닉네임 업데이트 요청")
public class UserNicknameUpdateRequest {

    @ApiModelProperty(position = 1, required = true, value = "닉네임", example = "김가볼까잇")
    @NotBlank(message = "닉네임은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String nickname;

    @Builder
    private UserNicknameUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
}