package com.hanghae99.onit_be.noti;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.TimeStamped;
import com.hanghae99.onit_be.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import static com.hanghae99.onit_be.noti.NotificationType.EVENT_PARTICIPANT;
import static com.hanghae99.onit_be.noti.NotificationType.PLAN_CRATED;

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

    private String title;

    private String message;

    @Column(nullable = false)
    private boolean isRead;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String participantName;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
