package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @OneToMany(mappedBy = "admin")
    @ToString.Exclude
    private Set<UserRole> userRoles = new HashSet<>();

    @Column(nullable = false, length = 15)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, length = 5)
    private String legalName;
    @Column(nullable = false)
    private LocalDate birthdate;
    @Column(nullable = false)
    private LocalDateTime lastRequestAt;
    private Boolean isApproved;

    @Builder
    public Admin(String username, String password, LocalDate birthdate, String legalName) {
        this.username = username;
        this.password = password;
        this.birthdate = birthdate;
        this.legalName = legalName;
        this.isApproved = null;

        this.lastRequestAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    @Builder(builderMethodName = "testBuilder", builderClassName = "testBuilder")
    private Admin(Long id, Role role) {
        this.id = id;
        this.username = "tester";

        this.userRoles.add(UserRole.builder()
                .admin(this)
                .role(Role.USER)
                .build());
        this.userRoles.add(UserRole.builder()
                .admin(this)
                .role(Role.ADMIN)
                .build());

        if (role.equals(Role.MASTER))
            this.userRoles.add(UserRole.builder()
                    .admin(this)
                    .role(Role.MASTER)
                    .build());
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void decideRegistration(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public void updateLastRequestAt() {
        this.lastRequestAt = LocalDateTime.now();
    }

    public Set<String> getRoles() {
        Set<String> roles = new HashSet<>();
        for(UserRole userRole : this.userRoles)
            roles.add(userRole.getRole().name());
        return roles;
    }
}
