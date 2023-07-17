package com.gabojait.gabojaitspring.profile.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@GroupSequence({EducationDefaultReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "학력 기본 요청")
public class EducationDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "학력 식별자")
    private Long educationId;

    @ApiModelProperty(position = 2, required = true, value = "학교명", example = "가보자잇대학교")
    @NotBlank(message = "학교명은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 3, max = 20, message = "학교명은 3~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String institutionName;

    @ApiModelProperty(position = 3, required = true, value = "시작일", notes = "string", example = "2000-01-01")
    @NotNull(message = "시작일은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedAt;

    @ApiModelProperty(position = 4, value = "종료일", notes = "string", example = "2000-01-02")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedAt;

    @ApiModelProperty(position = 5, required = true, value = "현재 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "현재 여부는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isCurrent;

    public Education toEntity(User user) {
        return Education.builder()
                .user(user)
                .institutionName(this.institutionName.trim())
                .startedAt(this.startedAt)
                .endedAt(this.endedAt)
                .isCurrent(this.isCurrent)
                .build();
    }
}
