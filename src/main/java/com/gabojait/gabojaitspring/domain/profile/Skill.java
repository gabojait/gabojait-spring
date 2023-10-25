package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import com.gabojait.gabojaitspring.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String skillName;
    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    private Level level;
    @Column(nullable = false)
    private Boolean isExperienced;

    @Builder
    private Skill(String skillName, Level level, boolean isExperienced, User user) {
        this.skillName = skillName;
        this.level = level;
        this.isExperienced = isExperienced;
        this.user = user;
    }

    public void update(String skillName, Level level, boolean isExperienced) {
        this.skillName = skillName;
        this.level = level;
        this.isExperienced = isExperienced;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill)) return false;
        Skill skill = (Skill) o;
        return Objects.equals(id, skill.id)
                && Objects.equals(user, skill.user)
                && Objects.equals(skillName, skill.skillName)
                && level == skill.level
                && Objects.equals(isExperienced, skill.isExperienced);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, skillName, level, isExperienced);
    }
}
