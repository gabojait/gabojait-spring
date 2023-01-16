package com.inuappcenter.gabojaitspring.project.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({ProjectSaveRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class})
@ApiModel(value = "Project 생성 요청")
public class ProjectSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트 이름", example = "가보자잇")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 20, message = "프로젝트 이름은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectName;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 설명", example = "사이드 프로젝트 팀원을 찾는 아이템입니다.")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 5, max = 60, message = "프로젝트 설명은 5~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectDescription;

    @ApiModelProperty(position = 3, required = true, value = "바라는 점", example = "열정 많은 팀원을 찾습니다.")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 5, max = 60, message = "바라는 점은 5~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String expectationDescription;

    @ApiModelProperty(position = 4, required = true, value = "링크", example = "gabojait.opentalk.com")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(max = 1000, message = "링크는 0~1000자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String chatLink;

    @ApiModelProperty(position = 4, required = true, value = "총 백엔드개발자 수", example = "1")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Byte totalBackendCnt;

    @ApiModelProperty(position = 5, required = true, value = "총 프론트엔드개발자 수", example = "1")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Byte totalFrontendCnt;

    @ApiModelProperty(position = 6, required = true, value = "총 디자이너 수", example = "1")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Byte totalDesignerCnt;

    @ApiModelProperty(position = 7, required = true, value = "총 매니저 수", example = "1")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Byte totalManagerCnt;

    public Project toEntity(ObjectId leader) {
        return Project.builder()
                .leader(leader)
                .projectName(this.projectName)
                .projectDescription(this.projectDescription)
                .expectationDescription(this.expectationDescription)
                .chatLink(this.chatLink)
                .totalBackendCnt(this.totalBackendCnt)
                .totalFrontendCnt(this.totalFrontendCnt)
                .totalDesignerCnt(this.totalDesignerCnt)
                .totalManagerCnt(this.totalManagerCnt)
                .build();
    }
}
