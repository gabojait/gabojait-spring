package com.gabojait.gabojaitspring.team.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({TeamCompleteUpdateReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "프로젝트 완료 요청")
public class TeamCompleteUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "완료한 프로젝트 URL", example = "github.com/gabojait")
    @NotBlank(message = "완료된 프로젝트 URL을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    String projectUrl;
}
