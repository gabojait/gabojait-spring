package com.gabojait.gabojaitspring.api.dto.profile.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.profile.Education;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "학력 업데이트 요청")
public class EducationUpdateRequest {

    @ApiModelProperty(position = 1, required = true, value = "학력 식별자")
    private Long educationId;

    @ApiModelProperty(position = 2, required = true, value = "학교명", example = "가보자잇대학교")
    @Size(min = 3, max = 20, message = "학교명은 3~20자만 가능합니다.")
    private String institutionName;

    @ApiModelProperty(position = 3, required = true, value = "시작일", notes = "string", example = "2000-01-01")
    @NotNull(message = "시작일은 필수 입력입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedAt;

    @ApiModelProperty(position = 4, value = "종료일", notes = "string", example = "2000-01-02")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedAt;

    @ApiModelProperty(position = 5, required = true, value = "현재 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "현재 여부는 필수 입력입니다.")
    private Boolean isCurrent;

    public Education toEntity(User user) {
        return Education.builder()
                .institutionName(this.institutionName.trim())
                .startedAt(this.startedAt)
                .endedAt(this.endedAt)
                .isCurrent(this.isCurrent)
                .user(user)
                .build();
    }

    @Builder
    private EducationUpdateRequest(Long educationId,
                                    String institutionName,
                                    LocalDate startedAt,
                                    LocalDate endedAt,
                                    Boolean isCurrent) {
        this.educationId = educationId;
        this.institutionName = institutionName;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EducationUpdateRequest that = (EducationUpdateRequest) o;
        return Objects.equals(educationId, that.educationId)
                && Objects.equals(institutionName, that.institutionName)
                && Objects.equals(startedAt, that.startedAt)
                && Objects.equals(endedAt, that.endedAt)
                && Objects.equals(isCurrent, that.isCurrent);
    }

    public int hashCode(User user) {
        return Objects.hash(educationId, user, institutionName, startedAt, endedAt, isCurrent);
    }
}
