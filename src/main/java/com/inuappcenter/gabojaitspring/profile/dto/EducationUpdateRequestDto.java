package com.inuappcenter.gabojaitspring.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@GroupSequence({EducationUpdateRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class,
        ValidationSequence.Pattern.class})
@ApiModel(value = "Education 수정 요청")
public class EducationUpdateRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "학력 식별자", allowableValues = "Restriction: [NotBlank]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String educationId;

    @ApiModelProperty(position = 2, required = true, value = "학교명", example = "가보자잇대학교",
            allowableValues = "Restriction: [NotBlank > Size]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 3, max = 30, message = "학교명은 3~30자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String institutionName;

    @ApiModelProperty(position = 3, required = true, notes = "string", value = "시작일", example = "2000-01-01",
            allowableValues = "Format: [yyyy-MM-dd], Restriction: [NotNull > Pattern]")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "올바른 날짜 형식이 아닙니다.",
            groups = ValidationSequence.Pattern.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedDate;

    @ApiModelProperty(position = 4, required = true, notes = "string", value = "종료일", example = "2000-01-02",
            allowableValues = "Format: [yyyy-MM-dd], Restriction: [NotNull > Pattern]")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "올바른 날짜 형식이 아닙니다.",
            groups = ValidationSequence.Pattern.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedDate;

    @ApiModelProperty(position = 4, required = true, value = "현재 여부: true, false", example = "true",
            allowableValues = "Input: [true | false], Restriction: [NotNull]")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    private Boolean isCurrent;
}
