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

    @Field(name = "project_name")
    private String projectName;

    @Field(name = "backends")
    private List<ObjectId> backends = new ArrayList<>();

    @Field(name = "total_backend_cnt")
    private Byte totalBackendCnt;

    @Field(name = "frontends")
    private List<ObjectId> frontends = new ArrayList<>();

    @Field(name = "total_frontend_cnt")
    private Byte totalFrontendCnt;

    @Field(name = "designers")
    private List<ObjectId> designers = new ArrayList<>();

    @Field(name = "total_designer_cnt")
    private Byte totalDesignerCnt;

    @Field(name = "managers")
    private List<ObjectId> managers = new ArrayList<>();

    @Field(name = "total_manager_cnt")
    private Byte totalManagerCnt;

    @Field(name = "project_description")
    private String projectDescription;

    @Field(name = "expectation_description")
    private String expectationDescription;

    @Field(name = "started_date")
    private LocalDateTime startedDate;

    @Field(name = "ended_date")
    private LocalDateTime endedDate;

    @Field(name = "chat_link")
    private String chatLink;

    @Field(name = "project_result_link")
    private String projectResultLink;

    private ObjectId leader;

    @Builder
    public Project(ObjectId leader,
                   String projectName,
                   String projectDescription,
                   String expectationDescription,
                   String chatLink,
                   Byte totalBackendCnt,
                   Byte totalFrontendCnt,
                   Byte totalDesignerCnt,
                   Byte totalManagerCnt) {
        this.leader = leader;
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

    public void addBackend(ObjectId profile) {
        this.backends.add(profile);
    }

    public void removeBackend(ObjectId profile) {
        this.backends.remove(profile);
    }

    public void addFrontend(ObjectId profile) {
        this.frontends.add(profile);
    }

    public void removeFrontend(ObjectId profile) {
        this.frontends.remove(profile);
    }

    public void addDesigner(ObjectId profile) {
        this.designers.add(profile);
    }

    public void removeDesigner(ObjectId profile) {
        this.designers.remove(profile);
    }

    public void addManager(ObjectId profile) {
        this.managers.add(profile);
    }

    public void removeManager(ObjectId profile) {
        this.managers.remove(profile);
    }
}
