package com.hanghae99.onit_be.noti;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.TimeStamped;
import com.hanghae99.onit_be.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.hanghae99.onit_be.noti.NotificationType.EVENT_PARTICIPANT;
import static com.hanghae99.onit_be.noti.NotificationType.PLAN_CRATED;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_Notification")
public class Notification extends TimeStamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "notification_id")
    private Long id;

    private String title;

    private String message;

    private boolean checked;

    private String url;

    @ManyToOne
    private User user;

    private String participantName;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;


    public Notification(Plan plan, User user) {
        this.title =plan.getPlanName();
        this.message = (plan.getPlanDate()+ "일정에 참여 하여습니다!");
        this.checked = false;
        this.url = plan.getUrl();
        this.user = user;
        this.participantName = user.getNickname();
        this.notificationType = EVENT_PARTICIPANT;
    }
}
