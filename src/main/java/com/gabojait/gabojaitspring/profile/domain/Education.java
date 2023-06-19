package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 20)
    private String institutionName;
    @Column(nullable = false)
    private LocalDate startedAt;
    private LocalDate endedAt;
    @Column(nullable = false)
    private Boolean isCurrent;

    @Builder
    public Education(String institutionName, LocalDate startedAt, LocalDate endedAt, boolean isCurrent, User user) {
        this.institutionName = institutionName;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
        this.user = user;
        this.isDeleted = false;
    }

    public void update(String institutionName, LocalDate startedAt, LocalDate endedAt, boolean isCurrent) {
        this.institutionName = institutionName;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
