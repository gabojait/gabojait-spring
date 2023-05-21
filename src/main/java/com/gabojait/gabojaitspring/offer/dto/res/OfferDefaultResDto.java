package com.gabojait.gabojaitspring.offer.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "제안 기본 응답")
public class OfferDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "제안 식별자")
    private String offerId;

    @ApiModelProperty(position = 2, required = true, value = "회원 식별자")
    private String userId;

    @ApiModelProperty(position = 3, required = true, value = "팀 식별자")
    private String teamId;

    @ApiModelProperty(position = 4, required = true, value = "승인 여부")
    private Boolean isAccepted;

    @ApiModelProperty(position = 5, required = true, value = "제안자")
    private String offeredBy;

    @ApiModelProperty(position = 6, required = true, value = "포지션")
    private String position;

    @ApiModelProperty(position = 7, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @ApiModelProperty(position = 8, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @ApiModelProperty(position = 9, required = true, value = "스키마 버전")
    private String schemaVersion;

    public OfferDefaultResDto(Offer offer) {
        this.offerId = offer.getId().toString();
        this.userId = offer.getUserId().toString();
        this.teamId = offer.getTeamId().toString();
        this.isAccepted = offer.getIsAccepted();
        this.offeredBy = OfferedBy.fromChar(offer.getOfferedBy()).name();
        this.position = Position.fromChar(offer.getPosition()).name();
        this.createdDate = offer.getCreatedDate();
        this.modifiedDate = offer.getModifiedDate();
        this.schemaVersion = offer.getSchemaVersion();
    }
}
