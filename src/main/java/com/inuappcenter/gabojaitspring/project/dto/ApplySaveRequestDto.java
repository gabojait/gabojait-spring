package com.inuappcenter.gabojaitspring.project.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.project.domain.Apply;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@GroupSequence({ApplySaveRequestDto.class, ValidationSequence.NotBlank.class, ValidationSequence.NotNull.class})
@ApiModel(value = "Apply 생성 요청")
public class ApplySaveRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트 식별자")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String projectId;

    @ApiModelProperty(position = 2, required = true, value = "포지션: D, B, F, M", example = "B",
            allowableValues = "D, B, F, M")
    @NotNull(message = "포지션을 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Character position;

    public Apply toEntity(ObjectId profileId, Position position) {
        return Apply.builder()
                .userProfileId(profileId)
                .projectId(new ObjectId(this.projectId))
                .position(position)
                .build();
    }
}
