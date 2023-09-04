package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.user.domain.type.Role;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @ToString.Exclude
    private Admin admin;

    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    private UserRole(User user, Admin admin, Role role) {
        this.user = user;
        this.admin = admin;
        this.role = role;

        if (user != null)
            this.user.getUserRoles().add(this);
        else
            this.admin.getUserRoles().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole)) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(id, userRole.id)
                && role == userRole.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role);
    }
}
