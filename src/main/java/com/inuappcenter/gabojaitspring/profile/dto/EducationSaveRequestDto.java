package com.inuappcenter.gabojaitspring.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@GroupSequence({EducationSaveRequestDto.class, ValidationSequence.NotBlank.class, ValidationSequence.NotNull.class})
@ApiModel(value = "Education 생성 요청")
public class EducationSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "프로필 식별자", example = "profileId")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    private String profileId;

    @ApiModelProperty(position = 2, dataType = "String", value = "학교명", example = "institutionName")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    private String institutionName;

    @ApiModelProperty(position = 3, required = true, dataType = "LocalDate", value = "시작일", example = "2000-01-01")
    @NotNull(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedDate;

    @ApiModelProperty(position = 4, required = true, dataType = "LocalDate", value = "종료일", example = "2000-01-01")
    @NotNull(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedDate;

    @ApiModelProperty(position = 5, required = true, dataType = "Boolean", value = "현재 진행 여부", example = "false")
    @NotNull(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotNull.class)
    private Boolean isCurrent;

    public Education toEntity() {
        return Education.ByEducationBuilder()
                .institutionName(institutionName)
                .startedDate(startedDate)
                .endedDate(endedDate)
                .isCurrent(isCurrent)
                .profileId(profileId)
                .build();
    }
}
