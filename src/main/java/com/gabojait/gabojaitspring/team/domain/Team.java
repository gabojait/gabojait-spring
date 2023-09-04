package com.gabojait.gabojaitspring.team.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @OneToMany(mappedBy = "team")
    @ToString.Exclude
    private List<TeamMember> teamMembers = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    @ToString.Exclude
    private List<FavoriteUser> favoriteUsers = new ArrayList<>();

    @Column(nullable = false, length = 20)
    private String projectName;
    @Column(nullable = false, length = 500)
    private String projectDescription;
    @Column(nullable = false)
    private Byte designerCnt;
    @Column(nullable = false)
    private Byte backendCnt;
    @Column(nullable = false)
    private Byte frontendCnt;
    @Column(nullable = false)
    private Byte managerCnt;
    @Column(nullable = false, length = 200)
    private String expectation;
    @Column(nullable = false, length = 100)
    private String openChatUrl;
    private String projectUrl;

    @Column(nullable = false)
    private Boolean isRecruiting;
    @Column(nullable = false)
    private Boolean isDesignerFull;
    @Column(nullable = false)
    private Boolean isBackendFull;
    @Column(nullable = false)
    private Boolean isFrontendFull;
    @Column(nullable = false)
    private Boolean isManagerFull;
    private LocalDateTime completedAt;
    @Column(nullable = false)
    private Long visitedCnt;

    @Builder
    private Team(String projectName,
                String projectDescription,
                Byte designerCnt,
                Byte backendCnt,
                Byte frontendCnt,
                Byte managerCnt,
                Boolean isDesignerFull,
                Boolean isBackendFull,
                Boolean isFrontendFull,
                Boolean isManagerFull,
                String expectation,
                String openChatUrl) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.designerCnt = designerCnt;
        this.backendCnt = backendCnt;
        this.frontendCnt = frontendCnt;
        this.managerCnt = managerCnt;
        this.expectation = expectation;
        this.openChatUrl = openChatUrl;

        this.projectUrl = null;
        this.isRecruiting = true;
        this.isDesignerFull = isDesignerFull;
        this.isBackendFull = isBackendFull;
        this.isFrontendFull = isFrontendFull;
        this.isManagerFull = isManagerFull;
        this.completedAt = null;
        this.visitedCnt = 0L;
        this.isDeleted = false;
    }

    public void update(String projectName,
                       String projectDescription,
                       String expectation,
                       String openChatUrl,
                       Map<Position, Byte> teamMemberRecruits) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.expectation = expectation;
        this.openChatUrl = openChatUrl;

        if (teamMemberRecruits.containsKey(Position.DESIGNER))
            this.designerCnt = teamMemberRecruits.get(Position.DESIGNER);
        else
            this.designerCnt = 0;
        if (teamMemberRecruits.containsKey(Position.BACKEND))
            this.backendCnt = teamMemberRecruits.get(Position.BACKEND);
        else
            this.backendCnt = 0;
        if (teamMemberRecruits.containsKey(Position.FRONTEND))
            this.frontendCnt = teamMemberRecruits.get(Position.FRONTEND);
        else
            this.frontendCnt = 0;
        if (teamMemberRecruits.containsKey(Position.MANAGER))
            this.managerCnt = teamMemberRecruits.get(Position.MANAGER);
        else
            this.managerCnt = 0;
    }

    public void updateIsPositionFull(Position position, List<TeamMember> teamMembers) {
        byte cnt = 0;

        for(TeamMember teamMember : teamMembers)
            if (position.equals(teamMember.getPosition()))
                cnt++;

        switch (position) {
            case DESIGNER:
                this.isDesignerFull = cnt >= this.designerCnt;
                break;
            case BACKEND:
                this.isBackendFull = cnt >= this.backendCnt;
                break;
            case FRONTEND:
                this.isFrontendFull = cnt >= this.frontendCnt;
                break;
            case MANAGER:
                this.isManagerFull = cnt >= this.managerCnt;
                break;
        }
    }

    public void updateIsRecruiting(boolean isRecruiting) {
        this.isRecruiting = isRecruiting;
    }

    public boolean isPositionFull(Position position) {
        switch (position.name()) {
            case "DESIGNER":
                return this.isDesignerFull;
            case "BACKEND":
                return this.isBackendFull;
            case "FRONTEND":
                return this.isFrontendFull;
            case "MANAGER":
                return this.isManagerFull;
            default:
                return true;
        }
    }

    public void incrementVisitedCnt() {
        this.visitedCnt++;
    }

    public void incomplete() {
        this.isDeleted = true;
    }

    public void complete(String projectUrl) {
        this.projectUrl = projectUrl;
        this.completedAt = LocalDateTime.now();
        this.isDeleted = true;
    }
}
