package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "프로필 요약 응답")
public class ProfileAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private String userId;

    @ApiModelProperty(position = 2, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 3, required = true, value = "포지션",
            allowableValues = "designer, backend, frontend, manager, none")
    private String position;

    @ApiModelProperty(position = 4, required = true, value = "리뷰 수")
    private Integer reviewCnt;

    @ApiModelProperty(position = 5, required = true, value = "평점")
    private Float rating;

    @ApiModelProperty(position = 6, required = true, value = "스키마 버전")
    private String schemaVersion;

    public ProfileAbstractResDto(User user) {
        this.userId = user.getId().toString();
        this.nickname = user.getNickname();
        this.position = Position.fromChar(user.getPosition()).name().toLowerCase();
        this.reviewCnt = user.getReviews().size();
        this.rating = user.getRating();
        this.schemaVersion = user.getSchemaVersion();
    }
}
