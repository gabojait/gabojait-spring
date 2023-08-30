package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

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
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 20)
    private String skillName;
    @Column(nullable = false)
    private Boolean isExperienced;
    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    private Level level;

    @Builder
    private Skill(String skillName, boolean isExperienced, Level level, User user) {
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level;
        this.user = user;
        this.isDeleted = false;
    }

    public void update(String skillName, boolean isExperienced, Level level) {
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level;
    }

    public void delete() {
        this.isDeleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return id.equals(skill.id)
                && user.equals(skill.user)
                && skillName.equals(skill.skillName)
                && isExperienced.equals(skill.isExperienced)
                && level == skill.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, skillName, isExperienced, level);
    }
}
