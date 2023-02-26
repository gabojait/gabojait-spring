package com.inuappcenter.gabojaitspring.profile.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@GroupSequence({UserProfileVisibilityDefaultReqDto.class,
        ValidationSequence.NotNull.class})
@ApiModel(value = "User 프로필 공개여부 기본 요청")
public class UserProfileVisibilityDefaultReqDto {
    @ApiModelProperty(position = 1, required = true, value = "공개 여부", example = "true", allowableValues = "true, false")
    @NotNull(message = "공개 여부를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isPublic;
}
