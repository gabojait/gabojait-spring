package com.inuappcenter.gabojaitspring.team.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Field(name = "project_manager_total_recruit_cnt")
    private Short projectManagerTotalRecruitCnt;

    @Field(name = "open_chat_url")
    private String openChatUrl;

    @Field(name = "is_public")
    private Boolean isPublic;

    private List<User> designers = new ArrayList<>();
    private List<User> backends = new ArrayList<>();
    private List<User> frontends = new ArrayList<>();
    private List<User> projectManagers = new ArrayList<>();
    private String expectation;
    private List<ObjectId> applications = new ArrayList<>();
    private List<ObjectId> recruits = new ArrayList<>();

    @Builder
    public Team(ObjectId leaderUserId,
                String projectName,
                String projectDescription,
                Short designerTotalRecruitCnt,
                Short backendTotalRecruitCnt,
                Short frontendTotalRecruitCnt,
                Short projectManagerTotalRecruitCnt,
                String openChatUrl,
                String expectation) {
        this.leaderUserId = leaderUserId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.designerTotalRecruitCnt = designerTotalRecruitCnt;
        this.backendTotalRecruitCnt = backendTotalRecruitCnt;
        this.frontendTotalRecruitCnt = frontendTotalRecruitCnt;
        this.projectManagerTotalRecruitCnt = projectManagerTotalRecruitCnt;
        this.openChatUrl = openChatUrl;
        this.expectation = expectation;
        this.isPublic = true;
        this.isDeleted = false;
    }

    public void addDesigner(User user) {
        this.designers.add(user);
    }

    public void removeDesigner(User user) {
        this.designers.remove(user);
    }

    public void addBackend(User user) {
        this.backends.add(user);
    }

    public void removeBackend(User user) {
        this.backends.remove(user);
    }

    public void addFrontend(User user) {
        this.frontends.add(user);
    }

    public void removeFrontend(User user) {
        this.frontends.remove(user);
    }

    public void addProjectManager(User user) {
        this.projectManagers.add(user);
    }

    public void removeProjectManagers(User user) {
        this.projectManagers.remove(user);
    }

    public void updateIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
