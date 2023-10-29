package com.gabojait.gabojaitspring.domain.user;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    private UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole)) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(id, userRole.id)
                && Objects.equals(user, userRole.user)
                && role == userRole.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, role);
    }
}
