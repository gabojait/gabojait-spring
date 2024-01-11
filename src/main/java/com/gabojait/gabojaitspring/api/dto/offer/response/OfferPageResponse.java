package com.gabojait.gabojaitspring.api.dto.offer.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.Position;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@ApiModel(value = "제안 페이지 응답")
public class OfferPageResponse {

    @ApiModelProperty(position = 1, required = true, value = "제안 식별자")
    private Long offerId;

    @ApiModelProperty(position = 2, required = true, value = "포지션",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER")
    private Position position;

    @ApiModelProperty(position = 3, required = true, value = "승인 여부", allowableValues = "true, false, null")
    private Boolean isAccepted;

    @ApiModelProperty(position = 4, required = true, value = "제안자", allowableValues = "USER, TEAM")
    private OfferedBy offeredBy;

    @ApiModelProperty(position = 5, required = true, value = "회원")
    private OfferUserResponse user;

    @ApiModelProperty(position = 6, required = true, value = "팀")
    private OfferTeamResponse team;

    @ApiModelProperty(position = 7, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 8, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public OfferPageResponse(Offer offer, List<Skill> skills) {
        this.offerId = offer.getId();
        this.position = offer.getPosition();
        this.isAccepted = offer.getIsAccepted();
        this.offeredBy = offer.getOfferedBy();
        this.user = new OfferUserResponse(offer.getUser(), skills);
        this.team = new OfferTeamResponse(offer.getTeam());
        this.createdAt = offer.getCreatedAt();
        this.updatedAt = offer.getUpdatedAt();
    }
}
