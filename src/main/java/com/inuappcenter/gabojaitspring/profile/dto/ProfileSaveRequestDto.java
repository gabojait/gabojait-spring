package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@GroupSequence({ProfileSaveRequestDto.class, ValidationSequence.NotBlank.class, ValidationSequence.Size.class})
@ApiModel(value = "Profile 요청")
public class ProfileSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "유저 식별자", example = "userId")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    private String userId;

    @ApiModelProperty(position = 2, dataType = "String", value = "소개글", example = "about")
    @Size(max = 100, message = "소개글은 100자 이하만 가능합니다", groups = ValidationSequence.Size.class)
    private String about;

    @ApiModelProperty(position = 3,
            dataType = "Character",
            allowableValues = "D, B, F, P",
            value = "포지션: D, B, F, P",
            example = "B")
    private Character position;

    public Profile toEntity() {
        return Profile.ByProfileBuilder()
                .userId(userId)
                .about(about)
                .position(position)
                .build();
    }
}

