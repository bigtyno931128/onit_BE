package com.hanghae99.onit_be.noti.event;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.noti.Notification;
import com.hanghae99.onit_be.noti.NotificationType;
import com.hanghae99.onit_be.noti.NotificationRepository;
import com.hanghae99.onit_be.mypage.ParticipantRepository;
import com.hanghae99.onit_be.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Async
@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final ParticipantRepository participantRepository;
    private final NotificationRepository notificationRepository;
    private final PlanRepository planRepository;

    @EventListener
    public void handleNotificationCreateEvent(NotificationEvent notificationEvent) {
        Plan plan = notificationEvent.getPlan();
        User user = notificationEvent.getUser();
        String message = (plan.getPlanDate() + "일정에 참여하였습니다.");
        createNotification(plan, user , message, NotificationType.EVENT_PARTICIPANT);
    }

    @EventListener
    public void handlePlanUpdateEvent(PlanUpdateEvent planUpdateEvent) {
        Plan plan = planRepository.findByUser(planUpdateEvent.getUser());
        createNotification(plan, plan.getUser() , planUpdateEvent.getMessage(), NotificationType.PLAN_UPDATE);
    }

    @EventListener
    public void handlePlanDeleteEvent(PlanDeleteEvent planDeleteEvent) {
        Plan plan = planRepository.findByUser(planDeleteEvent.getUser());
        createNotification(plan, plan.getUser() , planDeleteEvent.getMessage(), NotificationType.PLAN_DELETE);
    }

    private void createNotification(Plan plan, User user, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(plan.getPlanName());
        notification.setUrl(plan.getUrl());
        notification.setUser(user);
        notification.setParticipantName(user.getNickname());
        notification.setNotificationType(notificationType);
        notification.setMessage(message);
        notification.setRead(false);
        notificationRepository.save(notification);
    }
}
