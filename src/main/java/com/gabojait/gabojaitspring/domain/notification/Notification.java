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

    @Column(nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String body;
    @Column(nullable = false)
    private Boolean isRead;

    @Builder
    private Notification(User user, NotificationType notificationType, String title, String body) {
        this.user = user;
        this.notificationType = notificationType;
        this.title = title;
        this.body = body;
        this.isRead = false;
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
                && notificationType == that.notificationType
                && Objects.equals(title, that.title)
                && Objects.equals(body, that.body)
                && Objects.equals(isRead, that.isRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, notificationType, title, body, isRead);
    }
}
