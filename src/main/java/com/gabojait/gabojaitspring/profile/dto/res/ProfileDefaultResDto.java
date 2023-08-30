package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.dto.ProfileInfoDto;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@ApiModel(value = "프로필 기본 응답")
public class ProfileDefaultResDto extends ProfileAbstractResDto {

    @ApiModelProperty(position = 8, required = true, value = "자기소개")
    private String profileDescription;

    @ApiModelProperty(position = 9, required = true, value = "프로필 사진")
    private String imageUrl;

    @ApiModelProperty(position = 10, required = true, value = "리더 여부")
    private Boolean isLeader;

    @ApiModelProperty(position = 11, required = true, value = "팀 찾기 여부")
    private Boolean isSeekingTeam;

    @ApiModelProperty(position = 12, required = true, value = "리뷰")
    private List<ReviewDefaultResDto> reviews = new ArrayList<>();

    @ApiModelProperty(position = 13, required = true, value = "학력")
    private List<EducationDefaultResDto> educations = new ArrayList<>();

    @ApiModelProperty(position = 14, required = true, value = "포트폴리오")
    private List<PortfolioDefaultResDto> portfolios = new ArrayList<>();

    @ApiModelProperty(position = 15, required = true, value = "기술")
    private  List<SkillDefaultResDto> skills = new ArrayList<>();

    @ApiModelProperty(position = 16, required = true, value = "경력")
    private List<WorkDefaultResDto> works = new ArrayList<>();

    @ApiModelProperty(position = 17, required = true, value = "완료한 팀")
    private List<TeamAbstractResDto> completedTeams = new ArrayList<>();

    @ApiModelProperty(position = 18, required = true, value = "현재 팀")
    private TeamAbstractResDto currentTeam;

    public ProfileDefaultResDto(User user, ProfileInfoDto profileInfo) {
        super(user);

        this.profileDescription = user.getProfileDescription();
        this.imageUrl = user.getImageUrl();
        this.isSeekingTeam = user.getIsSeekingTeam();

        if (!user.getReceivedReviews().isEmpty()) {
            List<Review> reviews = user.getReceivedReviews();
            int reviewSize = reviews.size();

            for (int i = Math.min(reviewSize, 3); i > 0; i--)
                if (!reviews.get(i - 1).getIsDeleted())
                    this.reviews.add(new ReviewDefaultResDto(reviews.get(i - 1), i));
        }

        this.educations.addAll(profileInfo.getEducations());
        this.portfolios.addAll(profileInfo.getPortfolios());
        this.skills.addAll(profileInfo.getSkills());
        this.works.addAll(profileInfo.getWorks());
        this.completedTeams.addAll(profileInfo.getCompletedTeams());
        this.currentTeam = profileInfo.getCurrentTeam();
        this.isLeader = profileInfo.getIsLeader();
    }
}
