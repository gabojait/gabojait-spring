package com.gabojait.gabojaitspring.api.dto.team.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.user.Position;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "팀 제안 응답")
public class TeamOfferResponse {

    @ApiModelProperty(position = 1, required = true, value = "제안 식별자")
    private Long offerId;

    @ApiModelProperty(position = 2, required = true, value = "포지션",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER")
    private Position position;

    @ApiModelProperty(position = 3, required = true, value = "승인 여부", allowableValues = "true, false, null")
    private Boolean isAccepted;

    @ApiModelProperty(position = 4, required = true, value = "제안자", allowableValues = "USER, TEAM")
    private OfferedBy offeredBy;

    @ApiModelProperty(position = 5, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 6, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public TeamOfferResponse(Offer offer) {
        this.offerId = offer.getId();
        this.position = offer.getPosition();
        this.isAccepted = offer.getIsAccepted();
        this.offeredBy = offer.getOfferedBy();
        this.createdAt = offer.getCreatedAt();
        this.updatedAt = offer.getUpdatedAt();
    }
}
