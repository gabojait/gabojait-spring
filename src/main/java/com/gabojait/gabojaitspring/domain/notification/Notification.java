package com.gabojait.gabojaitspring.domain.notification;

import com.gabojait.gabojaitspring.domain.base.BasePermanentEntity;
import com.gabojait.gabojaitspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor
public class Notification extends BasePermanentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String body;
    @Column(nullable = false)
    private Boolean isRead;
    @Column(nullable = false, length = 31)
    @Enumerated(EnumType.STRING)
    private DeepLinkType deepLinkType;

    @Builder
    private Notification(User user, String title, String body, DeepLinkType deepLinkType) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.isRead = false;
        this.deepLinkType = deepLinkType;
        this.isDeleted = false;
    }

    public void read() {
        this.isRead = true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Notification)) return false;
        Notification that = (Notification) object;
        return Objects.equals(id, that.id)
                && Objects.equals(user, that.user)
                && deepLinkType == that.deepLinkType
                && Objects.equals(title, that.title)
                && Objects.equals(body, that.body)
                && Objects.equals(isRead, that.isRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, deepLinkType, title, body, isRead);
    }
}
