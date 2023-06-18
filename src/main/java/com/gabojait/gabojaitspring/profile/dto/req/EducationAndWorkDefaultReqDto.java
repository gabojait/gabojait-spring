package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidIfPresent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@ValidIfPresent
@ApiModel(value = "학력과 경력 기본 요청")
public class EducationAndWorkDefaultReqDto {

    @ApiModelProperty(position = 1, value = "생성 학력들")
    @Valid
    private List<EducationCreateReqDto> createEducations;

    @ApiModelProperty(position = 2, value = "수정 학력들")
    @Valid
    private List<EducationUpdateReqDto> updateEducations;

    @ApiModelProperty(position = 3, value = "삭제 학력 식별자들")
    private List<Long> deleteEducationIds;

    @ApiModelProperty(position = 4, value = "생성 경력들")
    @Valid
    private List<WorkCreateReqDto> createWorks;

    @ApiModelProperty(position = 5, value = "수정 경력들")
    @Valid
    private List<WorkUpdateReqDto> updateWorks;

    @ApiModelProperty(position = 6, value = "삭제 경력 식별자들")
    private List<Long> deleteWorkIds;
}
