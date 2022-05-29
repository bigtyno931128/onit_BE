package com.hanghae99.onit_be.noti;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.TimeStamped;
import com.hanghae99.onit_be.entity.User;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tbl_Notification")
public class Notification extends TimeStamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "notification_id")
    private Long id;

    private String message;

    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    public void update(Plan plan, User user, String message, NotificationType notificationType) {
        this.plan = plan;
        this.user = user;
        this.message = message;
        this.notificationType = notificationType;
        this.isRead = false;
    }
}
