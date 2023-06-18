package com.gabojait.gabojaitspring.profile.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "프로필 요약 응답")
public class ProfileAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private Long userId;

    @ApiModelProperty(position = 2, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 3, required = true, value = "포지션",
            allowableValues = "designer, backend, frontend, manager, none")
    private String position;

    @ApiModelProperty(position = 4, required = true, value = "리뷰 수")
    private Integer reviewCnt;

    @ApiModelProperty(position = 5, required = true, value = "평점")
    private Float rating;

    @ApiModelProperty(position = 6, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 7, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    public ProfileAbstractResDto(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.position = Position.fromChar(user.getPosition()).name();
        this.reviewCnt = user.getReviewCnt();
        this.rating = user.getRating();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
