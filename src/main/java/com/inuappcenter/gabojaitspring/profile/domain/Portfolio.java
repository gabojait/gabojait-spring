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

    @Field(name = "user_id")
    private ObjectId userId;

    @Field(name = "portfolio_type")
    private Character portfolioType;

    private String name;
    private String url;

    @Builder
    public Portfolio(ObjectId userId, PortfolioType portfolioType, String name, String url) {
        this.userId = userId;
        this.portfolioType = portfolioType.getType();
        this.name = name;
        this.url = url;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void update(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
