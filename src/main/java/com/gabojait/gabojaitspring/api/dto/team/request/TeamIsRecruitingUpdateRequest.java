package com.gabojait.gabojaitspring.api.dto.team.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({TeamIsRecruitingUpdateRequest.class, ValidationSequence.Blank.class})
@ApiModel(value = "팀원 모집 여부 수정 요청")
public class TeamIsRecruitingUpdateRequest {

    @ApiModelProperty(position = 1, required = true, value = "팀원 모집 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "팀원 모집 여부는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isRecruiting;

    @Builder
    private TeamIsRecruitingUpdateRequest(Boolean isRecruiting) {
        this.isRecruiting = isRecruiting;
    }
}
