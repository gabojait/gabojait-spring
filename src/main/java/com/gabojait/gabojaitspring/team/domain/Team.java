package com.gabojait.gabojaitspring.team.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String projectName;
    private String projectDescription;
    private Byte designerTotalRecruitCnt;
    private Byte backendTotalRecruitCnt;
    private Byte frontendTotalRecruitCnt;
    private Byte managerTotalRecruitCnt;
    private String expectation;
    private String openChatUrl;
    private String projectUrl;

    private Boolean isRecruiting;
    private Boolean isDesignerFull;
    private Boolean isBackendFull;
    private Boolean isFrontendFull;
    private Boolean isManagerFull;
    private LocalDateTime completedAt;
    private Long visitedCnt;
    private Integer userOfferCnt;
    private Integer teamOfferCnt;
    private Integer userJoinCnt;
    private Integer userFiredCnt;
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
    }

    public void updateIsPositionFull(Position position) {
        byte cnt = 0;

        for(TeamMember teamMember : this.teamMembers)
            if (position.getType().equals(teamMember.getPosition()))
                cnt++;

        switch (position.getType()) {
            case 'D':
                this.isDesignerFull = cnt >= this.designerTotalRecruitCnt;
                break;
            case 'B':
                this.isBackendFull = cnt >= this.backendTotalRecruitCnt;
                break;
            case 'F':
                this.isFrontendFull = cnt >= this.frontendTotalRecruitCnt;
                break;
            case 'M':
                this.isManagerFull = cnt >= this.managerTotalRecruitCnt;
                break;
        }
    }

    public void updateIsRecruiting(boolean isRecruiting) {
        this.isRecruiting = isRecruiting;
    }

    public boolean isPositionFull(Position position) {
        switch (position.getType()) {
            case 'D':
                return this.isDesignerFull;
            case 'B':
                return this.isBackendFull;
            case 'F':
                return this.isFrontendFull;
            case 'M':
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
