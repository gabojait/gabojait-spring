package com.gabojait.gabojaitspring.team.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@ToString
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

    @Field(name = "user_offer_cnt")
    private Long userOfferCnt;

    @Field(name = "team_offer_cnt")
    private Long teamOfferCnt;

    @Field(name = "teammate_join_cnt")
    private Long teammateJoinCnt;

    @Field(name = "teammate_fired_cnt")
    private Long teammateFiredCnt;

    @Field(name = "teammate_left_cnt")
    private Long teammateLeftCnt;

    @Field(name = "favorite_user_ids")
    private List<ObjectId> favoriteUserIds = new ArrayList<>();

    @Field(name = "manager_user_ids")
    private List<ObjectId> managerUserIds = new ArrayList<>();

    @Field(name = "designer_user_ids")
    private List<ObjectId> designerUserIds = new ArrayList<>();

    @Field(name = "backend_user_ids")
    private List<ObjectId> backendUserIds = new ArrayList<>();

    @Field(name = "frontend_user_ids")
    private List<ObjectId> frontendUserIds = new ArrayList<>();
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
        this.userOfferCnt = 0L;
        this.teamOfferCnt = 0L;

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

        this.isDesignerFull = this.designerTotalRecruitCnt <= this.designerUserIds.size();
        this.isBackendFull = this.backendTotalRecruitCnt <= this.backendUserIds.size();
        this.isFrontendFull = this.frontendTotalRecruitCnt <= this.frontendUserIds.size();
        this.isManagerFull = this.managerTotalRecruitCnt <= this.managerUserIds.size();
    }

    public void addTeammate(ObjectId userId, char position) {
        switch (position) {
            case 'D':
                this.designerUserIds.add(userId);
                this.isDesignerFull = this.designerTotalRecruitCnt <= this.designerUserIds.size();
                this.teammateJoinCnt++;
                break;
            case 'B':
                this.backendUserIds.add(userId);
                this.isBackendFull = this.backendTotalRecruitCnt <= this.backendUserIds.size();
                this.teammateJoinCnt++;
                break;
            case 'F':
                this.frontendUserIds.add(userId);
                this.isFrontendFull = this.frontendTotalRecruitCnt <= this.frontendUserIds.size();
                this.teammateJoinCnt++;
                break;
            case 'M':
                this.managerUserIds.add(userId);
                this.isManagerFull = this.managerTotalRecruitCnt <= this.managerUserIds.size();
                this.teammateJoinCnt++;
                break;
        }
    }

    public void removeTeammate(ObjectId userId, boolean isFired) {
        if (isFired) {
            this.teammateFiredCnt++;
        } else {
            this.teammateJoinCnt++;
        }

        if (!this.designerUserIds.isEmpty())
            for (ObjectId designerUserId : this.designerUserIds)
                if (userId.toString().equals(designerUserId.toString())) {
                    this.designerUserIds.remove(designerUserId);
                    this.isDesignerFull = this.designerTotalRecruitCnt <= this.designerUserIds.size();
                }
        if (!this.backendUserIds.isEmpty())
            for (ObjectId backendUserId : this.backendUserIds)
                if (userId.toString().equals(backendUserId.toString())) {
                    this.backendUserIds.remove(backendUserId);
                    this.isBackendFull = this.backendTotalRecruitCnt <= this.backendUserIds.size();
                }
        if (!this.frontendUserIds.isEmpty())
            for (ObjectId frontendUserId : this.frontendUserIds)
                if (userId.toString().equals(frontendUserId.toString())) {
                    this.frontendUserIds.remove(frontendUserId);
                    this.isFrontendFull = this.frontendTotalRecruitCnt <= this.frontendUserIds.size();
                }
        if (!this.managerUserIds.isEmpty())
            for (ObjectId managerUserId : this.managerUserIds)
                if (userId.toString().equals(managerUserId.toString())) {
                    this.managerUserIds.remove(managerUserId);
                    this.isManagerFull = this.managerTotalRecruitCnt <= this.managerUserIds.size();
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

    public List<ObjectId> getAllMembers() {
        List<ObjectId> teamMemberUserIds = new ArrayList<>();

        if (!this.designerUserIds.isEmpty())
            teamMemberUserIds.addAll(this.designerUserIds);
        if (!this.backendUserIds.isEmpty())
            teamMemberUserIds.addAll(this.backendUserIds);
        if (!this.frontendUserIds.isEmpty())
            teamMemberUserIds.addAll(this.frontendUserIds);
        if (!this.managerUserIds.isEmpty())
            teamMemberUserIds.addAll(this.managerUserIds);

        return teamMemberUserIds;
    }

    public boolean isLeader(String userId) {
        return this.leaderUserId.toString().equals(userId);
    }

    public boolean isTeamMember(String userId) {
        if (!this.designerUserIds.isEmpty())
            for (ObjectId designerUserId : this.designerUserIds)
                if (designerUserId.toString().equals(userId))
                    return true;
        if (!this.backendUserIds.isEmpty())
            for (ObjectId backendUserId : this.backendUserIds)
                if (backendUserId.toString().equals(userId))
                    return true;
        if (!this.frontendUserIds.isEmpty())
            for (ObjectId frontendUserId : this.frontendUserIds)
                if (frontendUserId.toString().equals(userId))
                    return true;
        if (!this.managerUserIds.isEmpty())
            for (ObjectId managerUserId : this.managerUserIds)
                if (managerUserId.toString().equals(userId))
                    return true;

        return false;
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

    public void offer(boolean isOfferedByUser) {
        if (isOfferedByUser)
            this.userOfferCnt++;
        else
            this.teamOfferCnt++;
    }

    /**
     * Notification related
     */

//    public Set<String> getAllMemberFcmTokens() {
//        List<User> teamMembers = this.getAllMembers();
//        Set<String> allMemberFcmTokens = new HashSet<>();
//
//        teamMembers.forEach(user -> {
//            if (user.getIsNotified())
//                allMemberFcmTokens.addAll(user.getFcmTokens());
//        });
//        return allMemberFcmTokens;
//    }
}
