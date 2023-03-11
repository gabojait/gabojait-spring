package com.inuappcenter.gabojaitspring.team.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({TeamCompleteUpdateReqDto.class, ValidationSequence.NotBlank.class, ValidationSequence.Size.class})
@ApiModel(value = "Team 종료 수정 요청")
public class TeamCompleteUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트 링크", example = "https://www.google.com/")
    @NotBlank(message = "프로젝트 링크는 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 1000, message = "프로젝트 링크는 1~1000자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectUrl;
}
