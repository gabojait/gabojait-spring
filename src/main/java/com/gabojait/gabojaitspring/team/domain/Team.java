package com.gabojait.gabojaitspring.team.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Document(collection = "team")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseTimeEntity {

    @Field(name = "leader_user_id")
    private ObjectId leaderUserId;

    @Field(name = "project_name")
    private String projectName;

    @Field(name = "project_description")
    private String projectDescription;

    @Field(name = "designer_total_recruit_cnt")
    private Short designerTotalRecruitCnt;

    @Field(name = "backend_total_recruit_cnt")
    private Short backendTotalRecruitCnt;

    @Field(name = "frontend_total_recruit_cnt")
    private Short frontendTotalRecruitCnt;

    @Field(name = "manager_total_recruit_cnt")
    private Short managerTotalRecruitCnt;

    @Field(name = "is_designer_full")
    private Boolean isDesignerFull;

    @Field(name = "is_backend_full")
    private Boolean isBackendFull;

    @Field(name = "is_frontend_full")
    private Boolean isFrontendFull;

    @Field(name = "is_manager_full")
    private Boolean isManagerFull;

    @Field(name = "open_chat_url")
    private String openChatUrl;

    @Field(name = "is_recruiting")
    private Boolean isRecruiting;

    @Field(name = "is_complete")
    private Boolean isComplete;

    @Field(name = "completed_date")
    private LocalDateTime completedDate;

    @Field(name = "project_url")
    private String projectUrl;

    @Field(name = "visited_cnt")
    private Long visitedCnt;

    @Field(name = "total_application_cnt")
    private Long totalApplicationCnt;

    @Field(name = "total_recruit_cnt")
    private Long totalRecruitCnt;

    @Field(name = "teammate_join_cnt")
    private Long teammateJoinCnt;

    @Field(name = "teammate_fired_cnt")
    private Long teammateFiredCnt;

    @Field(name = "teammate_left_cnt")
    private Long teammateLeftCnt;

    @Field(name = "user_offer_ids")
    private List<ObjectId> userOfferIds = new ArrayList<>();

    @Field(name = "team_offer_ids")
    private List<ObjectId> teamOfferIds = new ArrayList<>();

    @Field(name = "favorite_user_ids")
    private List<ObjectId> favoriteUserIds = new ArrayList<>();

    private List<User> managers = new ArrayList<>();
    private List<User> designers = new ArrayList<>();
    private List<User> backends = new ArrayList<>();
    private List<User> frontends = new ArrayList<>();
    private String expectation;

    @Builder
    public Team(ObjectId leaderUserId,
                String projectName,
                String projectDescription,
                Short designerTotalRecruitCnt,
                Short backendTotalRecruitCnt,
                Short frontendTotalRecruitCnt,
                Short managerTotalRecruitCnt,
                String openChatUrl,
                String expectation) {
        this.leaderUserId = leaderUserId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.designerTotalRecruitCnt = designerTotalRecruitCnt;
        this.backendTotalRecruitCnt = backendTotalRecruitCnt;
        this.frontendTotalRecruitCnt = frontendTotalRecruitCnt;
        this.managerTotalRecruitCnt = managerTotalRecruitCnt;
        this.openChatUrl = openChatUrl;
        this.expectation = expectation;

        this.isComplete = false;
        this.completedDate = null;
        this.isRecruiting = true;
        this.isDesignerFull = this.designerTotalRecruitCnt <= 0;
        this.isBackendFull = this.backendTotalRecruitCnt <= 0;
        this.isFrontendFull = this.frontendTotalRecruitCnt <= 0;
        this.isManagerFull = this.managerTotalRecruitCnt <= 0;

        this.visitedCnt = 0L;
        this.teammateJoinCnt = 0L;
        this.teammateFiredCnt = 0L;
        this.teammateLeftCnt = 0L;
        this.totalApplicationCnt = 0L;
        this.totalRecruitCnt = 0L;

        this.isDeleted = false;
    }

    public void updateTeam(String projectName,
                           String projectDescription,
                           Short designerTotalRecruitCnt,
                           Short backendTotalRecruitCnt,
                           Short frontendTotalRecruitCnt,
                           Short managerTotalRecruitCnt,
                           String expectation,
                           String openChatUrl) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.designerTotalRecruitCnt = designerTotalRecruitCnt;
        this.backendTotalRecruitCnt = backendTotalRecruitCnt;
        this.frontendTotalRecruitCnt = frontendTotalRecruitCnt;
        this.managerTotalRecruitCnt = managerTotalRecruitCnt;
        this.expectation = expectation;
        this.openChatUrl = openChatUrl;

        this.isDesignerFull = this.designerTotalRecruitCnt <= this.designers.size();
        this.isBackendFull = this.backendTotalRecruitCnt <= this.backends.size();
        this.isFrontendFull = this.frontendTotalRecruitCnt <= this.frontends.size();
        this.isManagerFull = this.managerTotalRecruitCnt <= this.managers.size();
    }

    public void addTeammate(User user, char position) {
        switch (position) {
            case 'D':
                this.designers.add(user);
                this.isDesignerFull = this.designerTotalRecruitCnt <= this.designers.size();
                this.teammateJoinCnt++;
                break;
            case 'B':
                this.backends.add(user);
                this.isBackendFull = this.backendTotalRecruitCnt <= this.backends.size();
                this.teammateJoinCnt++;
                break;
            case 'F':
                this.frontends.add(user);
                this.isFrontendFull = this.frontendTotalRecruitCnt <= this.frontends.size();
                this.teammateJoinCnt++;
                break;
            case 'M':
                this.managers.add(user);
                this.isManagerFull = this.managerTotalRecruitCnt <= this.managers.size();
                this.teammateJoinCnt++;
                break;
        }
    }

    public void removeTeammate(User user, boolean isFired) {
        if (isFired) {
            this.teammateFiredCnt++;
        } else {
            this.teammateJoinCnt++;
        }

        if (!this.designers.isEmpty())
            for (User designer : this.designers)
                if (user.getId().toString().equals(designer.getId().toString())) {
                    this.designers.remove(designer);
                    this.isDesignerFull = this.designerTotalRecruitCnt <= this.designers.size();
                }
        if (!this.backends.isEmpty())
            for (User backend : this.backends)
                if (user.getId().toString().equals(backend.getId().toString())) {
                    this.backends.remove(backend);
                    this.isBackendFull = this.backendTotalRecruitCnt <= this.backends.size();
                }
        if (!this.frontends.isEmpty())
            for (User frontend : this.frontends)
                if (user.getId().toString().equals(frontend.getId().toString())) {
                    this.frontends.remove(frontend);
                    this.isFrontendFull = this.frontendTotalRecruitCnt <= this.frontends.size();
                }
        if (!this.managers.isEmpty())
            for (User manager : this.managers)
                if (user.getId().toString().equals(manager.getId().toString())) {
                    this.managers.remove(manager);
                    this.isManagerFull = this.managerTotalRecruitCnt <= this.managers.size();
                }
    }

    public void updateFavoriteUserId(ObjectId userId, boolean isAddFavorite) {
        if (isAddFavorite) {
            if (!this.favoriteUserIds.contains(userId))
                this.favoriteUserIds.add(userId);
        } else {
            this.favoriteUserIds.remove(userId);
        }
    }

    public void updateIsRecruiting(Boolean isRecruiting) {
        this.isRecruiting = isRecruiting;
    }

    public List<User> getAllMembersExceptLeader(Team team) {
        List<User> teamMembers = new ArrayList<>();

        if (!team.getDesigners().isEmpty())
            teamMembers.addAll(team.getDesigners());
        if (!team.getBackends().isEmpty())
            teamMembers.addAll(team.getBackends());
        if (!team.getFrontends().isEmpty())
            teamMembers.addAll(team.getFrontends());
        if (!team.getManagers().isEmpty())
            teamMembers.addAll(team.getManagers());

        return teamMembers;
    }

    public boolean isLeader(String userId) {
        return this.leaderUserId.toString().equals(userId);
    }

    public void complete(String projectUrl) {
        this.projectUrl = projectUrl;
        this.completedDate = LocalDateTime.now();
        this.isRecruiting = false;
        this.isComplete = true;
    }

    public void incrementVisitedCnt() {
        this.visitedCnt++;
    }

    public void delete() {
        this.isRecruiting = false;
        this.isDeleted = true;
    }

    /**
     * Offer related
     */

    public void offer(ObjectId offerId, boolean isOfferedByUser) {
        if (isOfferedByUser)
            this.userOfferIds.add(offerId);
        else
            this.teamOfferIds.add(offerId);
    }
}
