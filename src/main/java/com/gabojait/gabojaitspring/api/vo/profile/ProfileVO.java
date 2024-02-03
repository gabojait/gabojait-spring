package com.gabojait.gabojaitspring.api.vo.profile;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.domain.profile.Education;
import com.gabojait.gabojaitspring.domain.profile.Portfolio;
import com.gabojait.gabojaitspring.domain.profile.Work;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import lombok.Getter;

import java.util.List;

@Getter
public final class ProfileVO {

    private final List<Education> educations;
    private final List<Portfolio> portfolios;
    private final List<Work> works;
    private final List<TeamMember> teamMembers;
    private final PageData<List<Review>> reviews;
    private final long reviewCnt;

    public ProfileVO(List<Education> educations,
                     List<Portfolio> portfolios,
                     List<Work> works,
                     List<TeamMember> teamMembers,
                     PageData<List<Review>> reviews,
                     long reviewCnt) {
        this.educations = educations;
        this.portfolios = portfolios;
        this.works = works;
        this.teamMembers = teamMembers;
        this.reviews = reviews;
        this.reviewCnt = reviewCnt;
    }
}
