package com.gabojait.gabojaitspring.offer.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "제안 기본 응답")
public class OfferDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "제안 식별자")
    private Long offerId;

    @ApiModelProperty(position = 2, required = true, value = "회원")
    private ProfileAbstractResDto user;

    @ApiModelProperty(position = 3, required = true, value = "팀")
    private TeamAbstractResDto team;

    @ApiModelProperty(position = 4, required = true, value = "승인 여부")
    private Boolean isAccepted;

    @ApiModelProperty(position = 5, required = true, value = "제안자", allowableValues = "user, team")
    private String offeredBy;

    @ApiModelProperty(position = 6, required = true, value = "포지션",
            allowableValues = "designer, backend, frontend, manager")
    private String position;

    @ApiModelProperty(position = 7, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 8, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public OfferDefaultResDto(Offer offer) {
        this.offerId = offer.getId();
        this.user = new ProfileAbstractResDto(offer.getUser());
        this.team = new TeamAbstractResDto(offer.getTeam());
        this.isAccepted = offer.getIsAccepted();
        this.offeredBy = OfferedBy.fromChar(offer.getOfferedBy()).name().toLowerCase();
        this.position = Position.fromChar(offer.getPosition()).name().toLowerCase();
        this.createdAt = offer.getCreatedAt();
        this.updatedAt = offer.getUpdatedAt();
    }
}
