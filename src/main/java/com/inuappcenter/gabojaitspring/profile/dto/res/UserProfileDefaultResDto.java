package com.inuappcenter.gabojaitspring.profile.dto.res;

import com.inuappcenter.gabojaitspring.profile.domain.*;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import com.inuappcenter.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.user.domain.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "User 프로필 기본 응답")
public class UserProfileDefaultResDto extends UserProfileAbstractResDto {

    @ApiModelProperty(position = 7, required = true, value = "자기소개")
    private String description;

    @ApiModelProperty(position = 8, required = true, value = "프로필 사진")
    private String imageUrl;

    @ApiModelProperty(position = 9, required = true, value = "리뷰")
    private List<ReviewDefaultResDto> reviews = new ArrayList<>();

    @ApiModelProperty(position = 10, required = true, value = "학력")
    private List<EducationDefaultResDto> educations = new ArrayList<>();

    @ApiModelProperty(position = 11, required = true, value = "경력")
    private List<WorkDefaultResDto> works = new ArrayList<>();

    @ApiModelProperty(position = 12, required = true, value = "기술")
    private  List<SkillDefaultResDto> skills = new ArrayList<>();

    @ApiModelProperty(position = 13, required = true, value = "포트폴리오")
    private List<PortfolioDefaultResDto> portfolios = new ArrayList<>();

    @ApiModelProperty(position = 14, required = true, value = "현재 팀 식별자")
    private String currentTeamId;

    @ApiModelProperty(position = 15, required = true, value = "완료한 팀")
    private List<UserTeamAbstractResDto> completedTeams = new ArrayList<>();

    @ApiModelProperty(position = 16, required = true, value = "팀 멤버 상태")
    private String teamMemberStatus;

    @ApiModelProperty(position = 17, required = true, value = "공개 여부")
    private Boolean isPublic;

    public UserProfileDefaultResDto(User user, List<Team> completedTeams) {
        super(user);

        this.description = user.getDescription();
        this.imageUrl = user.getImageUrl();
        this.teamMemberStatus = TeamMemberStatus.toEnum(user.getTeamMemberStatus()).name();
        this.isPublic = user.getIsPublic();

        if (user.getCurrentTeamId() != null)
            this.currentTeamId = user.getCurrentTeamId().toString();

        for (Review review : user.getReviews())
            this.reviews.add(new ReviewDefaultResDto(review));

        for (Education education : user.getEducations())
            this.educations.add(new EducationDefaultResDto(education));

        for (Work work : user.getWorks())
            this.works.add(new WorkDefaultResDto(work));

        for (Skill skill : user.getSkills())
            this.skills.add(new SkillDefaultResDto(skill));

        for (Portfolio portfolio : user.getPortfolios())
            this.portfolios.add(new PortfolioDefaultResDto(portfolio));

        if (completedTeams != null)
            for (Team team : completedTeams)
                this.completedTeams.add(new UserTeamAbstractResDto(team, user));
    }
}
