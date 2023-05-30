package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@ToString
@Document(collection = "portfolio")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseTimeEntity {

    @Field(name = "user_id")
    private ObjectId userId;

    @Field(name = "portfolio_name")
    private String portfolioName;

    private Character media;
    private String url;

    @Builder
    public Portfolio(ObjectId userId, Media media, String portfolioName, String url) {
        this.userId = userId;
        this.media = media.getType();
        this.portfolioName = portfolioName;
        this.url = url;
        this.isDeleted = false;
    }

    public void update(String portfolioName, String url) {
        this.portfolioName = portfolioName;
        this.url = url;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
