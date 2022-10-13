package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Document(collection = "profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseTimeEntity {

    private String about;

    private Integer position;

    private List<Education> education = new ArrayList<>();

    private List<Work> work = new ArrayList<>();

    private List<Skill> skill = new ArrayList<>();

    private List<Long> review = new ArrayList<>();
}
