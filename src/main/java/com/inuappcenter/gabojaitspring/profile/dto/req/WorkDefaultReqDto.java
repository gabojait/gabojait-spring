package com.inuappcenter.gabojaitspring.profile.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
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
@GroupSequence({WorkDefaultReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class})
@ApiModel(value = "Work 기본 요청")
public class WorkDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "기관명", example = "가보자잇회사")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 20, message = "기관명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String corporationName;

    @ApiModelProperty(position = 2, required = true, notes = "string", value = "시작일", example = "2000-01-01")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedDate;

    @ApiModelProperty(position = 3, required = true, notes = "string", value = "종료일", example = "2000-01-02")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedDate;

    @ApiModelProperty(position = 4, required = true, value = "현재 여부: true, false", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isCurrent;

    @ApiModelProperty(position = 5, value = "설명", example = "가보자잇에서 백엔드 개발")
    @Size(max = 100, message = "설명은 0~100자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String description;

    public Work toEntity(ObjectId userId) {
        return Work.builder()
                .userId(userId)
                .corporationName(this.corporationName)
                .startedDate(this.startedDate)
                .endedDate(this.endedDate)
                .isCurrent(this.isCurrent)
                .description(this.description)
                .build();
    }
}
