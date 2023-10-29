package com.gabojait.gabojaitspring.api.dto.profile.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import com.gabojait.gabojaitspring.domain.profile.Work;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({WorkDefaultRequest.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "경력 생성 요청")
public class WorkDefaultRequest {

    @ApiModelProperty(position = 1, required = true, value = "경력 식별자")
    private Long workId;

    @ApiModelProperty(position = 2, required = true, value = "기관명", example = "가보자잇사")
    @NotBlank(message = "기관명은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 20, message = "기관명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String corporationName;

    @ApiModelProperty(position = 3, value = "경력 설명", example = "가보자잇에서 백엔드 개발")
    @Size(max = 100, message = "경력 설명은 0~100자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String workDescription;

    @ApiModelProperty(position = 4, required = true, value = "시작일", notes = "string", example = "2000-01-01")
    @NotNull(message = "시작일은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedAt;

    @ApiModelProperty(position = 5, value = "종료일", notes = "string", example = "2000-01-02")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedAt;

    @ApiModelProperty(position = 6, required = true, value = "현재 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "현재 여부은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isCurrent;

    public Work toEntity(User user) {
        return Work.builder()
                .corporationName(this.corporationName.trim())
                .workDescription(this.workDescription.trim())
                .startedAt(this.startedAt)
                .endedAt(this.endedAt)
                .isCurrent(this.isCurrent)
                .user(user)
                .build();
    }

    @Builder
    private WorkDefaultRequest(Long workId,
                               String corporationName,
                               String workDescription,
                               LocalDate startedAt,
                               LocalDate endedAt,
                               Boolean isCurrent) {
        this.workId = workId;
        this.corporationName = corporationName;
        this.workDescription = workDescription;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkDefaultRequest that = (WorkDefaultRequest) o;
        return Objects.equals(workId, that.workId)
                && Objects.equals(corporationName, that.corporationName)
                && Objects.equals(workDescription, that.workDescription)
                && Objects.equals(startedAt, that.startedAt)
                && Objects.equals(endedAt, that.endedAt)
                && Objects.equals(isCurrent, that.isCurrent);
    }

    public int hashCode(User user) {
        return Objects.hash(workId, user, corporationName, workDescription, startedAt, endedAt, isCurrent);
    }
}
