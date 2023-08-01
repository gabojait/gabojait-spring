package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.user.domain.type.Role;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity(name = "member_role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }
}
