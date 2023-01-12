package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({ProfileSaveRequestDto.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class})
@ApiModel(value = "Profile 생성 요청")
public class ProfileSaveRequestDto {

    @ApiModelProperty(position = 1, value = "자기소개", example = "저는 가보자잇팀입니다.")
    @Size(max = 40, message = "자기소개는 0~40자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String description;

    @ApiModelProperty(position = 2, required = true, value = "포지션: D, B, F, M", example = "B",
            allowableValues = "D, B, F, M")
    @NotNull(message = "포지션을 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Character position;

    public Profile toEntity(Position position) {
        return Profile.builder()
                .description(this.description)
                .position(position)
                .build();
    }
}
