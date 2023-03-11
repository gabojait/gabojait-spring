package com.inuappcenter.gabojaitspring.offer.dto.res;

import com.inuappcenter.gabojaitspring.offer.domain.Offer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "Offer 기본 응답")
public class OfferDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "제안 식별자")
    private String offerId;

    @ApiModelProperty(position = 2, required = true, value = "지원자 식별자")
    private String applicantId;

    @ApiModelProperty(position = 3, required = true, value = "팀 식별자")
    private String teamId;

    @ApiModelProperty(position = 4, required = true, value = "지원자로부터 제안 식별")
    private Boolean isByApplicant;

    @ApiModelProperty(position = 5, required = true, value = "수락 여부")
    private Boolean isAccepted;

    @ApiModelProperty(position = 6, required = true, value = "생성일")
    private LocalDateTime createdDate;

    @ApiModelProperty(position = 7, required = true, value = "수정일")
    private LocalDateTime modifiedDate;

    @ApiModelProperty(position = 8, required = true, value = "스키마 버전")
    private String schemaVersion;

    public OfferDefaultResDto(Offer offer) {
        this.offerId = offer.getId().toString();
        this.applicantId = offer.getApplicantId().toString();
        this.teamId = offer.getTeamId().toString();
        this.isByApplicant = offer.getIsByApplicant();
        this.createdDate = offer.getCreatedDate();
        this.modifiedDate = offer.getModifiedDate();
        this.schemaVersion = offer.getSchemaVersion();

        if (offer.getIsAccepted() != null)
            this.isAccepted = offer.getIsAccepted();
    }
}
