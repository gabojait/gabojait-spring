package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.gabojait.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "프로필 기본 응답")
public class ProfileDefaultResDto extends ProfileAbstractResDto {

    @ApiModelProperty(position = 7, required = true, value = "자기소개")
    private String profileDescription;

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

    @ApiModelProperty(position = 14, required = true, value = "완료한 팀")
    private List<TeamAbstractResDto> completedTeams = new ArrayList<>();

    @ApiModelProperty(position = 15, required = true, value = "현재 팀 식별자")
    private String currentTeamId;

    @ApiModelProperty(position = 16, required = true, value = "팀 멤버 상태")
    private String teamMemberStatus;

    @ApiModelProperty(position = 17, required = true, value = "공개 여부")
    private Boolean isPublic;

    public ProfileDefaultResDto(User user, List<Team> completedTeams) {
        super(user);

        this.profileDescription = user.getProfileDescription();
        this.imageUrl = user.getImageUrl();
        this.teamMemberStatus = TeamMemberStatus.toEnum(user.getTeamMemberStatus()).name();
        this.isPublic = user.getIsPublic();

        if (user.getReviews() != null)
            user.getReviews().forEach(r -> this.reviews.add(new ReviewDefaultResDto(r)));
        if (user.getEducations() != null)
            user.getEducations().forEach(e -> this.educations.add(new EducationDefaultResDto(e)));
        if (user.getWorks() != null)
            user.getWorks().forEach(w -> this.works.add(new WorkDefaultResDto(w)));
        if (user.getSkills() != null)
            user.getSkills().forEach(s -> this.skills.add(new SkillDefaultResDto(s)));
        if (user.getPortfolios() != null)
            user.getPortfolios().forEach(p -> this.portfolios.add(new PortfolioDefaultResDto(p)));
        if (completedTeams != null)
            completedTeams.forEach(t -> this.completedTeams.add(new TeamAbstractResDto(t)));

        if (user.getCurrentTeamId() != null)
            this.currentTeamId = user.getCurrentTeamId().toString();
    }
}
