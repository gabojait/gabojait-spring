package com.gabojait.gabojaitspring.api.dto.profile.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "팀 찾기 여부 수정 요청")
public class ProfileIsSeekRequest {

    @ApiModelProperty(position = 1, required = true, value = "팀 찾기 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "팀 찾기 여부는 필수 입력입니다.")
    private Boolean isSeekingTeam;

    @Builder
    private ProfileIsSeekRequest(Boolean isSeekingTeam) {
        this.isSeekingTeam = isSeekingTeam;
    }
}
