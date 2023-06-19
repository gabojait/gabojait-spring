package com.gabojait.gabojaitspring.profile.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({WorkCreateReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "경력 생성 요청")
public class WorkCreateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "기관명", example = "가보자잇사")
    @NotBlank(message = "기관명을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 20, message = "기관명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String corporationName;

    @ApiModelProperty(position = 2, required = true, value = "시작일", notes = "string", example = "2000-01-01")
    @NotNull(message = "시작일을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedAt;

    @ApiModelProperty(position = 3, value = "종료일", notes = "string", example = "2000-01-02")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedAt;

    @ApiModelProperty(position = 4, required = true, value = "현재 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "현재 여부를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private Boolean isCurrent;

    @ApiModelProperty(position = 5, value = "경력 설명", example = "가보자잇에서 백엔드 개발")
    @Size(max = 100, message = "경력 설명은 0~100자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String workDescription;

    public Work toEntity(User user) {
        return Work.builder()
                .user(user)
                .corporationName(this.corporationName)
                .startedAt(this.startedAt)
                .endedAt(this.endedAt)
                .isCurrent(this.isCurrent)
                .workDescription(this.workDescription)
                .build();
    }
}
