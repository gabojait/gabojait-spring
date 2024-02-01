package com.gabojait.gabojaitspring.api.dto.profile.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.api.vo.profile.ProfileVO;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@ToString
@ApiModel(value = "프로필 본인 조회 응답")
public class ProfileFindMyselfResponse {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private Long userId;

    @ApiModelProperty(position = 2, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 3, required = true, value = "포지션",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER, NONE")
    private Position position;

    @ApiModelProperty(position = 4, required = true, value = "리뷰 수")
    private Integer reviewCnt;

    @ApiModelProperty(position = 5, required = true, value = "평점")
    private Float rating;

    @ApiModelProperty(position = 6, required = true, value = "프로필 사진")
    private String imageUrl;

    @ApiModelProperty(position = 7, required = true, value = "자기소개")
    private String profileDescription;

    @ApiModelProperty(position = 8, required = true, value = "리더 여부")
    private Boolean isLeader;

    @ApiModelProperty(position = 9, required = true, value = "팀 찾기 여부")
    private Boolean isSeekingTeam;

    @ApiModelProperty(position = 10, required = true, value = "기술들")
    private List<SkillResponse> skills;

    @ApiModelProperty(position = 11, required = true, value = "학력들")
    private List<EducationResponse> educations;

    @ApiModelProperty(position = 12, required = true, value = "포트폴리오들")
    private List<PortfolioResponse> portfolios;

    @ApiModelProperty(position = 13, required = true, value = "경력들")
    private List<WorkResponse> works;

    @ApiModelProperty(position = 14, required = true, value = "완료한 팀들")
    private List<ProfileTeamResponse> completedTeams;

    @ApiModelProperty(position = 15, required = true, value = "현재 팀")
    private ProfileTeamResponse currentTeam;

    @ApiModelProperty(position = 16, required = true, value = "리뷰들")
    private List<ProfileReviewResponse> reviews;

    @ApiModelProperty(position = 17, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 18, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    public ProfileFindMyselfResponse(User user, List<Skill> skills, ProfileVO profile) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.position = user.getPosition();
        this.reviewCnt = user.getReviewCnt();
        this.rating = user.getRating();
        this.imageUrl = user.getImageUrl();
        this.profileDescription = user.getProfileDescription();
        this.isSeekingTeam = user.getIsSeekingTeam();

        this.skills = skills.stream()
                .map(SkillResponse::new)
                .collect(Collectors.toList());
        this.educations = profile.getEducations().stream()
                .map(EducationResponse::new)
                .collect(Collectors.toList());
        this.portfolios = profile.getPortfolios().stream()
                .map(PortfolioResponse::new)
                .collect(Collectors.toList());
        this.works = profile.getWorks().stream()
                .map(WorkResponse::new)
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

        this.reviews = IntStream.range(0, profile.getReviews().getData().size())
                .mapToObj(i ->
                        new ProfileReviewResponse(profile.getReviews().getData().get(i), (int) (profile.getReviewCnt() - i))
                ).collect(Collectors.toList());

        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
