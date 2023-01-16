package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "Profile 요약 응답")
public class ProfileAbstractResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "프로필 식별자")
    private String profileId;

    @ApiModelProperty(position = 2, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 3, required = true, value = "포지션")
    private Character position;

    @ApiModelProperty(position = 4, required = true, value = "프로필 사진")
    private String imageUrl;

    @ApiModelProperty(position = 5, required = true, value = "평점")
    private Float rating;

    @ApiModelProperty(position = 6, required = true, value = "스키마버전")
    private String schemaVersion;

    public ProfileAbstractResponseDto(Profile profile) {
        this.profileId = profile.getId().toString();
        this.nickname = profile.getNickname();
        this.position = profile.getPosition();
        this.imageUrl = profile.getImageUrl();
        this.rating = profile.getRating();
        this.schemaVersion = profile.getSchemaVersion();
    }
}
