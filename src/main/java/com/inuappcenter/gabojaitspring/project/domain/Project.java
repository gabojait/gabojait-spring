package com.inuappcenter.gabojaitspring.project.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
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
@Document(collection = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {

    @Field(name = "leader_profile_id")
    private ObjectId leaderProfileId;

    @Field(name = "project_name")
    private String projectName;

    @Field(name = "backend_profile_ids")
    private List<ObjectId> backendProfileIds = new ArrayList<>();

    @Field(name = "total_backend_cnt")
    private Byte totalBackendCnt;

    @Field(name = "frontend_profile_ids")
    private List<ObjectId> frontendProfileIds = new ArrayList<>();

    @Field(name = "total_frontend_cnt")
    private Byte totalFrontendCnt;

    @Field(name = "designer_profile_ids")
    private List<ObjectId> designerProfileIds = new ArrayList<>();

    @Field(name = "total_designer_cnt")
    private Byte totalDesignerCnt;

    @Field(name = "manager_profile_ids")
    private List<ObjectId> managerProfileIds = new ArrayList<>();

    @Field(name = "total_manager_cnt")
    private Byte totalManagerCnt;

    @Field(name = "project_description")
    private String projectDescription;

    @Field(name = "expectation_description")
    private String expectationDescription;

    @Field(name = "apply_ids")
    private List<ObjectId> applyIds = new ArrayList<>();

    @Field(name = "recruit_ids")
    private List<ObjectId> recruitIds = new ArrayList<>();

    @Field(name = "started_date")
    private LocalDateTime startedDate;

    @Field(name = "ended_date")
    private LocalDateTime endedDate;

    @Field(name = "chat_link")
    private String chatLink;

    @Field(name = "project_result_link")
    private String projectResultLink;

    @Builder
    public Project(ObjectId leaderProfileId,
                   String projectName,
                   String projectDescription,
                   String expectationDescription,
                   String chatLink,
                   Byte totalBackendCnt,
                   Byte totalFrontendCnt,
                   Byte totalDesignerCnt,
                   Byte totalManagerCnt) {
        this.leaderProfileId = leaderProfileId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.expectationDescription = expectationDescription;
        this.chatLink = chatLink;
        this.projectResultLink = null;
        this.totalBackendCnt = totalBackendCnt;
        this.totalFrontendCnt = totalFrontendCnt;
        this.totalDesignerCnt = totalDesignerCnt;
        this.totalManagerCnt = totalManagerCnt;
        this.isDeleted = false;
    }

    public void update(String projectName,
                       String projectDescription,
                       String expectationDescription,
                       String chatLink,
                       Byte totalBackendCnt,
                       Byte totalFrontendCnt,
                       Byte totalDesignerCnt,
                       Byte totalManagerCnt) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.expectationDescription = expectationDescription;
        this.chatLink = chatLink;
        this.totalBackendCnt = totalBackendCnt;
        this.totalFrontendCnt = totalFrontendCnt;
        this.totalDesignerCnt = totalDesignerCnt;
        this.totalManagerCnt = totalManagerCnt;
    }

    public void startProject(LocalDateTime startedDate) {
        this.startedDate = startedDate;
    }

    public void endProject(LocalDateTime endedDate) {
        this.endedDate = endedDate;
    }

    public void setProjectResultLink(String projectResultLink) {
        this.projectResultLink = projectResultLink;
    }

    public void addBackend(ObjectId profileId) {
        this.backendProfileIds.add(profileId);
    }

    public void removeBackend(ObjectId profileId) {
        this.backendProfileIds.remove(profileId);
    }

    public void addFrontend(ObjectId profileId) {
        this.frontendProfileIds.add(profileId);
    }

    public void removeFrontend(ObjectId profileId) {
        this.frontendProfileIds.remove(profileId);
    }

    public void addDesigner(ObjectId profileId) {
        this.designerProfileIds.add(profileId);
    }

    public void removeDesigner(ObjectId profileId) {
        this.designerProfileIds.remove(profileId);
    }

    public void addManager(ObjectId profileId) {
        this.managerProfileIds.add(profileId);
    }

    public void removeManager(ObjectId profileId) {
        this.managerProfileIds.remove(profileId);
    }

    public void addApply(ObjectId applyId) {
        this.applyIds.add(applyId);
    }

    public void addRecruit(ObjectId recruitId) {
        this.recruitIds.add(recruitId);
    }
}
