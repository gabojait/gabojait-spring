package com.inuappcenter.gabojaitspring.profile.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@ApiModel(value = "Education과 Work 기본 요청")
public class EducationAndWorkDefaultReqDto {

    @ApiModelProperty(position = 1, value = "생성 학력들")
    private List<EducationCreateReqDto> createEducations;

    @ApiModelProperty(position = 2, value = "수정 학력들")
    private List<EducationUpdateReqDto> updateEducations;

    @ApiModelProperty(position = 3, value = "삭제 학력들")
    private List<String> deleteEducations;

    @ApiModelProperty(position = 4, value = "생성 경력들")
    private List<WorkCreateReqDto> createWorks;

    @ApiModelProperty(position = 5, value = "수정 경력들")
    private List<WorkUpdateReqDto> updateWorks;

    @ApiModelProperty(position = 6, value = "삭제 경력들")
    private List<String> deleteWorks;


}
