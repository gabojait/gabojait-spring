package com.gabojait.gabojaitspring.profile.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.exception.ValidationSequence;
import com.gabojait.gabojaitspring.profile.domain.Education;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@GroupSequence({EducationCreateReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "학력 생성 요청")
public class EducationCreateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "학교명", example = "가보자잇대학교")
    @NotBlank(message = "학교명을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @Size(min = 3, max = 20, message = "학교명은 3~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String institutionName;

    @ApiModelProperty(position = 2, required = true, value = "시작일", notes = "string", example = "2000-01-01")
    @NotNull(message = "시작일을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedDate;

    @ApiModelProperty(position = 3, required = true, value = "종료일", notes = "string", example = "2000-01-02")
    @NotNull(message = "종료일을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedDate;

    @ApiModelProperty(position = 4, required = true, value = "현재 여부", example = "true", allowableValues = "true, false")
    @NotNull(message = "현재 여부를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private Boolean isCurrent;

    public Education toEntity(ObjectId userId) {
        return Education.builder()
                .userId(userId)
                .institutionName(this.institutionName)
                .startedDate(this.startedDate)
                .endedDate(this.endedDate)
                .isCurrent(this.isCurrent)
                .build();
    }
}
