package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 20)
    private String skillName;
    @Column(nullable = false)
    private Boolean isExperienced;
    @Column(nullable = false)
    private Character level;

    @Builder
    public Skill(String skillName, boolean isExperienced, Level level, User user) {
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level.getType();
        this.user = user;
        this.isDeleted = false;
    }

    public void update(String skillName, boolean isExperienced, Level level) {
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level.getType();
    }

    public void delete() {
        this.isDeleted = true;
    }
}
