package com.inuappcenter.gabojaitspring.profile.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@ApiModel(value = "Position과 Skill 기본 요청")
public class PositionAndSkillDefaultReqDto {

    @ApiModelProperty(position = 1, value = "수정 포지션")
    private String position;

    @ApiModelProperty(position = 2, value = "생성 기술들")
    private List<SkillCreateReqDto> createSkills;

    @ApiModelProperty(position = 3, value = "수정 기술들")
    private List<SkillUpdateReqDto> updateSkills;

    @ApiModelProperty(position = 4, access = "삭제 기술들")
    private List<String> deleteSkills;
}
