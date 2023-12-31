package com.gabojait.gabojaitspring.api.dto.profile.response;

import com.gabojait.gabojaitspring.api.vo.profile.ProfileVO;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@ToString
@ApiModel(value = "프로필 기본 응답")
public class ProfileDefaultResponse extends ProfileAbstractResponse {

    @ApiModelProperty(position = 10, required = true, value = "자기소개")
    private String profileDescription;

    @ApiModelProperty(position = 11, required = true, value = "리더 여부")
    private Boolean isLeader;

    @ApiModelProperty(position = 12, required = true, value = "팀 찾기 여부")
    private Boolean isSeekingTeam;

    @ApiModelProperty(position = 13, required = true, value = "학력")
    private List<EducationDefaultResponse> educations;

    @ApiModelProperty(position = 14, required = true, value = "포트폴리오")
    private List<PortfolioDefaultResponse> portfolios;

    @ApiModelProperty(position = 15, required = true, value = "경력")
    private List<WorkDefaultResponse> works;

    @ApiModelProperty(position = 16, required = true, value = "완료한 팀")
    private List<ProfileTeamResponse> completedTeams;

    @ApiModelProperty(position = 17, required = true, value = "현재 팀")
    private ProfileTeamResponse currentTeam;

    @ApiModelProperty(position = 18, required = true, value = "리뷰")
    private List<ProfileReviewResponse> reviews;

    public ProfileDefaultResponse(User user,
                                  List<Skill> skills,
                                  ProfileVO profile) {
        super(user, skills);

        this.profileDescription = user.getProfileDescription();
        this.isSeekingTeam = user.getIsSeekingTeam();

        this.educations = profile.getEducations().stream()
                .map(EducationDefaultResponse::new)
                .collect(Collectors.toList());
        this.portfolios = profile.getPortfolios().stream()
                .map(PortfolioDefaultResponse::new)
                .collect(Collectors.toList());
        this.works = profile.getWorks().stream()
                .map(WorkDefaultResponse::new)
                .collect(Collectors.toList());

        this.completedTeams = profile.getTeamMembers().stream()
                .filter(tm -> tm.getTeam().getCompletedAt() != null)
                .map(tm -> new ProfileTeamResponse(tm.getTeam()))
                .collect(Collectors.toList());
        this.isLeader = false;
        profile.getTeamMembers().stream()
                .filter(tm -> tm.getTeam().getCompletedAt() == null)
                .findFirst()
                .ifPresent(tm -> {
                    this.currentTeam = new ProfileTeamResponse(tm.getTeam());
                    this.isLeader = tm.getIsLeader();
                });

        this.reviews = IntStream.range(0, profile.getReviews().getContent().size())
                .mapToObj(i ->
                        new ProfileReviewResponse(profile.getReviews().getContent().get(i), (int) (profile.getReviewCnt() - i)))
                .collect(Collectors.toList());
    }
}
