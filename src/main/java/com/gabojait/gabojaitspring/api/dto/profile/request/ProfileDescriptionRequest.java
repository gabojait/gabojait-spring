package com.gabojait.gabojaitspring.api.dto.profile.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "프로필 자기소개 수정 요청")
public class ProfileDescriptionRequest {

    @ApiModelProperty(position = 1, required = true, value = "자기소개", example = "김가보자잇 자기소개 입니다.")
    @Size(max = 120, message = "자기소개는 0~120자만 가능합니다.")
    private String profileDescription;

    @Builder
    private ProfileDescriptionRequest(String profileDescription) {
        this.profileDescription = profileDescription;
    }
}
