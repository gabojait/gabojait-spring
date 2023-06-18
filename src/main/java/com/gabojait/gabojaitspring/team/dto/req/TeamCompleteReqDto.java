package com.gabojait.gabojaitspring.team.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({TeamCompleteReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "프로젝트 완료 요청")
public class TeamCompleteReqDto {

    @ApiModelProperty(position = 1, required = true, value = "완료한 프로젝트 URL", example = "github.com/gabojait")
    @NotBlank(message = "완료된 프로젝트 URL은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    String projectUrl;
}
