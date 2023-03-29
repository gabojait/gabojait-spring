package com.inuappcenter.gabojaitspring.user.dto.res;

import com.inuappcenter.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "User 상세 응답")
public class UserDetailResDto extends UserDefaultResDto {

    @ApiModelProperty(position = 11, required = true, value = "찜 여부")
    private Boolean isFavorite;

    public UserDetailResDto(User user, boolean isFavorite) {
        super(user);

        this.isFavorite = isFavorite;
    }
}
