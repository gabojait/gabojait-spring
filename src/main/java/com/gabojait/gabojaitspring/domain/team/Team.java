package com.gabojait.gabojaitspring.domain.team;

import com.gabojait.gabojaitspring.domain.base.BasePermanentEntity;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BasePermanentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String projectName;
    @Column(nullable = false, length = 500)
    private String projectDescription;
    @Column(nullable = false, length = 200)
    private String expectation;
    @Column(nullable = false, length = 100)
    private String openChatUrl;
    private String projectUrl;
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private Byte designerCurrentCnt;
    @Column(nullable = false)
    private Byte backendCurrentCnt;
    @Column(nullable = false)
    private Byte frontendCurrentCnt;
    @Column(nullable = false)
    private Byte managerCurrentCnt;
    @Column(nullable = false)
    private Byte designerMaxCnt;
    @Column(nullable = false)
    private Byte backendMaxCnt;
    @Column(nullable = false)
    private Byte frontendMaxCnt;
    @Column(nullable = false)
    private Byte managerMaxCnt;
    @Column(nullable = false)
    private Long visitedCnt;
    @Column(nullable = false)
    private Boolean isRecruiting;

    @Builder
    private Team(String projectName,
                 String projectDescription,
                 String expectation,
                 String openChatUrl,
                 byte designerMaxCnt,
                 byte backendMaxCnt,
                 byte frontendMaxCnt,
                 byte managerMaxCnt) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.expectation = expectation;
        this.openChatUrl = openChatUrl;
        this.projectUrl = null;
        this.completedAt = null;
        this.designerCurrentCnt = 0;
        this.backendCurrentCnt = 0;
        this.frontendCurrentCnt = 0;
        this.managerCurrentCnt = 0;
        this.designerMaxCnt = designerMaxCnt;
        this.backendMaxCnt = backendMaxCnt;
        this.frontendMaxCnt = frontendMaxCnt;
        this.managerMaxCnt = managerMaxCnt;
        this.visitedCnt = 0L;
        this.isRecruiting = true;
        this.isDeleted = false;
    }

    public void update(String projectName,
                       String projectDescription,
                       String expectation,
                       byte designerMaxCnt,
                       byte backendMaxCnt,
                       byte frontendMaxCnt,
                       byte managerMaxCnt) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.expectation = expectation;
        if (this.designerCurrentCnt <= designerMaxCnt)
            this.designerMaxCnt = designerMaxCnt;
        else
            throw new CustomException(DESIGNER_CNT_UPDATE_UNAVAILABLE);
        if (this.backendCurrentCnt <= backendMaxCnt)
            this.backendMaxCnt = backendMaxCnt;
        else
            throw new CustomException(BACKEND_CNT_UPDATE_UNAVAILABLE);
        if (this.frontendCurrentCnt <= frontendMaxCnt)
            this.frontendMaxCnt = frontendMaxCnt;
        else
            throw new CustomException(FRONTEND_CNT_UPDATE_UNAVAILABLE);
        if (this.managerCurrentCnt <= managerMaxCnt)
            this.managerMaxCnt = managerMaxCnt;
        else
            throw new CustomException(MANAGER_CNT_UPDATE_UNAVAILABLE);
    }

    protected void join(Position position) {
        if (isPositionFull(position))
            throw new CustomException(TEAM_POSITION_UNAVAILABLE);

        switch (position) {
            case DESIGNER:
                this.designerCurrentCnt++;
                break;
            case BACKEND:
                this.backendCurrentCnt++;
                break;
            case FRONTEND:
                this.frontendCurrentCnt++;
                break;
            case MANAGER:
                this.managerCurrentCnt++;
                break;
        }

        boolean isDesignerFull = isPositionFull(Position.DESIGNER);
        boolean isBackendFull = isPositionFull(Position.BACKEND);
        boolean isFrontendFull = isPositionFull(Position.FRONTEND);
        boolean isManagerFull = isPositionFull(Position.MANAGER);

        if (isDesignerFull && isBackendFull && isFrontendFull && isManagerFull)
            this.isRecruiting = false;
    }

    protected void leave(Position position) {
        switch (position) {
            case DESIGNER:
                this.designerCurrentCnt--;
                break;
            case BACKEND:
                this.backendCurrentCnt--;
                break;
            case FRONTEND:
                this.frontendCurrentCnt--;
                break;
            case MANAGER:
                this.managerCurrentCnt--;
                break;
        }

        this.isRecruiting = true;
    }

    public void updateIsRecruiting(boolean isRecruiting) {
        this.isRecruiting = isRecruiting;
    }

    public boolean isPositionFull(Position position) {
        boolean isPositionFull = true;
        switch (position) {
            case DESIGNER:
                isPositionFull = this.designerMaxCnt <= this.designerCurrentCnt;
                break;
            case BACKEND:
                isPositionFull = this.backendMaxCnt <= this.backendCurrentCnt;
                break;
            case FRONTEND:
                isPositionFull = this.frontendMaxCnt <= this.frontendCurrentCnt;
                break;
            case MANAGER:
                isPositionFull = this.managerMaxCnt <= this.managerCurrentCnt;
                break;
        }
        return isPositionFull;
    }

    public void visit() {
        this.visitedCnt++;
    }

    public void incomplete() {
        this.isRecruiting = false;
        this.isDeleted = true;
    }

    public void complete(String projectUrl, LocalDateTime completedAt) {
        this.projectUrl = projectUrl;
        this.completedAt = completedAt;
        this.isRecruiting = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id)
                && Objects.equals(projectName, team.projectName)
                && Objects.equals(projectDescription, team.projectDescription)
                && Objects.equals(expectation, team.expectation)
                && Objects.equals(openChatUrl, team.openChatUrl)
                && Objects.equals(projectUrl, team.projectUrl)
                && Objects.equals(completedAt, team.completedAt)
                && Objects.equals(designerCurrentCnt, team.designerCurrentCnt)
                && Objects.equals(backendCurrentCnt, team.backendCurrentCnt)
                && Objects.equals(frontendCurrentCnt, team.frontendCurrentCnt)
                && Objects.equals(managerCurrentCnt, team.managerCurrentCnt)
                && Objects.equals(designerMaxCnt, team.designerMaxCnt)
                && Objects.equals(backendMaxCnt, team.backendMaxCnt)
                && Objects.equals(frontendMaxCnt, team.frontendMaxCnt)
                && Objects.equals(managerMaxCnt, team.managerMaxCnt)
                && Objects.equals(visitedCnt, team.visitedCnt)
                && Objects.equals(isRecruiting, team.isRecruiting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectName, projectDescription, expectation, openChatUrl, projectUrl,
                completedAt, designerCurrentCnt, backendCurrentCnt, frontendCurrentCnt, managerCurrentCnt,
                designerMaxCnt, backendMaxCnt, frontendMaxCnt, managerMaxCnt, visitedCnt, isRecruiting);
    }
}
