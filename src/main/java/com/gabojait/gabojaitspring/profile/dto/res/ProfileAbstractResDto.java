package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "프로필 요약 응답")
public class ProfileAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "사용자 식별자")
    private String userId;

    @ApiModelProperty(position = 2, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 3, required = true, value = "포지션")
    private String position;

    @ApiModelProperty(position = 4, required = true, value = "리뷰 수")
    private Integer reviewCnt;

    @ApiModelProperty(position = 5, required = true, value = "평점")
    private Float rating;

    @ApiModelProperty(position = 6, required = true, value = "스키마버전")
    private String schemaVersion;

    public ProfileAbstractResDto(User user) {
        this.userId = user.getId().toString();
        this.nickname = user.getNickname();
        this.reviewCnt = user.getReviews().size();
        this.rating = user.getRating();
        this.schemaVersion = user.getSchemaVersion();

        if (user.getPosition() != null)
            this.position = Position.fromChar(user.getPosition()).name();
    }
}
