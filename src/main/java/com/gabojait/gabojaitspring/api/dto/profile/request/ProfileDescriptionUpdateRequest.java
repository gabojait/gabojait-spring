package com.gabojait.gabojaitspring.api.dto.profile.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({ProfileDescriptionUpdateRequest.class,
        ValidationSequence.Size.class,})
@ApiModel(value = "프로필 자기소개 수정 요청")
public class ProfileDescriptionUpdateRequest {

    @ApiModelProperty(position = 1, required = true, value = "자기소개", example = "김가보자잇 자기소개 입니다.")
    @Size(max = 120, message = "자기소개는 0~120자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String profileDescription;

    @Builder
    private ProfileDescriptionUpdateRequest(String profileDescription) {
        this.profileDescription = profileDescription;
    }
}
