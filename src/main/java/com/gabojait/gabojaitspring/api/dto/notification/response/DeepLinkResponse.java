package com.gabojait.gabojaitspring.api.dto.notification.response;

import com.gabojait.gabojaitspring.domain.notification.DeepLinkType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "딥링크 응답")
public class DeepLinkResponse {

    @ApiModelProperty(position = 1, required = true, value = "URL")
    private String url;

    @ApiModelProperty(position = 2, required = true, value = "설명")
    private String description;

    public DeepLinkResponse(DeepLinkType deepLinkType) {
        this.description = deepLinkType.getDescription();
        this.url = deepLinkType.getUrl();
    }
}
