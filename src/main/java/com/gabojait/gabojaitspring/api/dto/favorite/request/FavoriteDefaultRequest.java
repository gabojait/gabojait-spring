package com.gabojait.gabojaitspring.api.dto.favorite.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({FavoriteDefaultRequest.class, ValidationSequence.Blank.class})
@ApiModel(value = "찜 기본 요청")
public class FavoriteDefaultRequest {

    @ApiModelProperty(position = 1, required = true, value = "찜 추가 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "찜 추가 여부는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isAddFavorite;

    public Favorite toFavoriteUserEntity(User user, User favoriteUser) {
        return Favorite.builder()
                .user(user)
                .favoriteUser(favoriteUser)
                .build();
    }

    public Favorite toFavoriteTeamEntity(User user, Team favoriteTeam) {
        return Favorite.builder()
                .user(user)
                .favoriteTeam(favoriteTeam)
                .build();
    }

    @Builder
    private FavoriteDefaultRequest(Boolean isAddFavorite) {
        this.isAddFavorite = isAddFavorite;
    }
}
