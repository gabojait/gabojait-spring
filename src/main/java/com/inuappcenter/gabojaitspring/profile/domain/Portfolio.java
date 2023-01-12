package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "portfolio")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseTimeEntity {

    @Field(name = "portfolio_type")
    private Character portfolioType;

    @Field(name = "profile_id")
    private ObjectId profileId;

    private String name;
    private String url;

    @Builder
    public Portfolio(PortfolioType portfolioType, String name, String url, ObjectId profileId) {
        this.portfolioType = portfolioType.getType();
        this.name = name;
        this.url = url;
        this.profileId = profileId;
        this.isDeleted = false;
    }

    public void deletePortfolio() {
        this.isDeleted  = true;
    }

    public void updatePortfolio(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
