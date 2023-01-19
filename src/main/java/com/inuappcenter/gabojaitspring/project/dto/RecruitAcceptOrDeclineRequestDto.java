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
@GroupSequence({RecruitAcceptOrDeclineRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class})
@ApiModel(value = "Recruit 수락/거절 요청")
public class RecruitAcceptOrDeclineRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "영입 식별자")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String recruitId;

    @ApiModelProperty(position = 2, required = true, value = "수락 여부: true, false", example = "true")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isAccepted;
}
