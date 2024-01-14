package com.gabojait.gabojaitspring.api.dto.favorite.request;

import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "찜 업데이트 요청")
public class FavoriteUpdateRequest {

    @ApiModelProperty(position = 1, required = true, value = "찜 추가 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "찜 추가 여부는 필수 입력입니다.")
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
    private FavoriteUpdateRequest(Boolean isAddFavorite) {
        this.isAddFavorite = isAddFavorite;
    }
}
