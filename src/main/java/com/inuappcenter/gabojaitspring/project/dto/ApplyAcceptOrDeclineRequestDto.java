package com.inuappcenter.gabojaitspring.project.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@GroupSequence({ApplyAcceptOrDeclineRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class})
@ApiModel(value = "Apply 수락/거절 요청")
public class ApplyAcceptOrDeclineRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "지원 식별자")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String applyId;

    @ApiModelProperty(position = 2, required = true, value = "수락 여부")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isAccepted;
}
