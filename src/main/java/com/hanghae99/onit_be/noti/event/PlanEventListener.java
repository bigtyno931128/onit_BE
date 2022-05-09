package com.hanghae99.onit_be.noti.event;

import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.noti.Notification;
import com.hanghae99.onit_be.repository.NotificationRepository;
import com.hanghae99.onit_be.repository.ParticipantRepository;
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
public class PlanEventListener {

    private final ParticipantRepository participantRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handlePlanCreateEvent(PlanCreateEvent planCreateEvent) {

        //List<Notification> notificationList = new ArrayList<>();

        // event 로 일정을 등록할때의 유저와 아이디 값은 가져옴 .
        System.out.println(planCreateEvent.getPlan().getId());
        System.out.println(planCreateEvent.getUser().getId());
        Plan plan = planCreateEvent.getPlan();
        User user = planCreateEvent.getUser();

        Notification notification = new Notification(plan,user);

        notificationRepository.save(notification);

        throw new RuntimeException();
    }
}
