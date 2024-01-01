package com.gabojait.gabojaitspring.api.dto.offer.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@ApiModel(value = "제안 회원 응답")
public class OfferUserResponse {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private Long userId;

    @ApiModelProperty(position = 2, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 3, required = true, value = "포지션",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER, NONE")
    private Position position;

    @ApiModelProperty(position = 4, required = true, value = "리뷰 수")
    private Integer reviewCnt;

    @ApiModelProperty(position = 5, required = true, value = "평점")
    private Float rating;

    @ApiModelProperty(position = 6, required = true, value = "프로필 사진")
    private String imageUrl;

    @ApiModelProperty(position = 7, required = true, value = "기술")
    private List<OfferSkillResponse> skills;

    @ApiModelProperty(position = 8, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 9, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    public OfferUserResponse(User user, List<Skill> skills) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.position = user.getPosition();
        this.reviewCnt = user.getReviewCnt();
        this.rating = user.getRating();
        this.imageUrl = user.getImageUrl();

        this.skills = skills.stream()
                .map(OfferSkillResponse::new)
                .collect(Collectors.toList());

        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
