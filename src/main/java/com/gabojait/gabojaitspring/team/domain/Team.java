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
    private Byte designerTotalRecruitCnt;
    @Column(nullable = false)
    private Byte backendTotalRecruitCnt;
    @Column(nullable = false)
    private Byte frontendTotalRecruitCnt;
    @Column(nullable = false)
    private Byte managerTotalRecruitCnt;
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
    @Column(nullable = false)
    private Integer userOfferCnt;
    @Column(nullable = false)
    private Integer teamOfferCnt;
    @Column(nullable = false)
    private Integer userJoinCnt;
    @Column(nullable = false)
    private Integer userFiredCnt;
    @Column(nullable = false)
    private Integer userLeftCnt;

    @Builder
    public Team(String projectName,
                String projectDescription,
                Byte designerTotalRecruitCnt,
                Byte backendTotalRecruitCnt,
                Byte frontendTotalRecruitCnt,
                Byte managerTotalRecruitCnt,
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

        this.projectUrl = null;
        this.isRecruiting = true;
        this.isDesignerFull = false;
        this.isBackendFull = false;
        this.isFrontendFull = false;
        this.isManagerFull = false;
        this.completedAt = null;
        this.visitedCnt = 0L;
        this.userOfferCnt = 0;
        this.teamOfferCnt = 0;
        this.userJoinCnt = 0;
        this.userFiredCnt = 0;
        this.userLeftCnt = 0;
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
            this.designerTotalRecruitCnt = teamMemberRecruits.get(Position.DESIGNER);
        else
            this.designerTotalRecruitCnt = 0;
        if (teamMemberRecruits.containsKey(Position.BACKEND))
            this.backendTotalRecruitCnt = teamMemberRecruits.get(Position.BACKEND);
        else
            this.backendTotalRecruitCnt = 0;
        if (teamMemberRecruits.containsKey(Position.FRONTEND))
            this.frontendTotalRecruitCnt = teamMemberRecruits.get(Position.FRONTEND);
        else
            this.frontendTotalRecruitCnt = 0;
        if (teamMemberRecruits.containsKey(Position.MANAGER))
            this.managerTotalRecruitCnt = teamMemberRecruits.get(Position.MANAGER);
        else
            this.managerTotalRecruitCnt = 0;
    }

    public void updateIsPositionFull(Position position) {
        byte cnt = 0;

        for(TeamMember teamMember : this.teamMembers)
            if (position.equals(teamMember.getPosition()))
                cnt++;

        switch (position.name()) {
            case "DESIGNER":
                this.isDesignerFull = cnt >= this.designerTotalRecruitCnt;
                break;
            case "BACKEND":
                this.isBackendFull = cnt >= this.backendTotalRecruitCnt;
                break;
            case "FRONTEND":
                this.isFrontendFull = cnt >= this.frontendTotalRecruitCnt;
                break;
            case "MANAGER":
                this.isManagerFull = cnt >= this.managerTotalRecruitCnt;
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

    public void incrementUserOfferCnt() {
        this.userOfferCnt++;
    }

    public void incrementTeamOfferCnt() {
        this.teamOfferCnt++;
    }

    public void incrementUserJoinCnt() {
        this.userJoinCnt++;
    }

    public void incrementUserFiredCnt() {
        this.userFiredCnt++;
    }

    public void incrementUserLeftCnt() {
        this.userLeftCnt++;
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
