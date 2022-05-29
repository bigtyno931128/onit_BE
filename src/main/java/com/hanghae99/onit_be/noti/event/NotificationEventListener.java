package com.hanghae99.onit_be.noti.event;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.noti.*;
import com.hanghae99.onit_be.mypage.ParticipantRepository;
import com.hanghae99.onit_be.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


@Async
@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private Set<SseEmitter> emitterSet = new CopyOnWriteArraySet<>();
    private final ParticipantRepository participantRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final EmitterRepository emitterRepository;



    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleParticipateEvent(ParticipateEvent event) {
        log.info(event.toString());
        log.info("111111111111111번");
        
        Optional<Notification> notification = notificationRepository.
                findByUserAndPlanAndAndNotificationType(event.getUser(),event.getPlan()
                        ,NotificationType.PARTICIPATE);

        if(!notification.isPresent()) {
            createNotification(event.getPlan(),event.getUser(), event.getMessage(), NotificationType.PARTICIPATE);

        }

        // send ()
        log.info("신규 알림 = {}, 알림 구독자 수 = {}", event.getUser(), emitterSet.size());

//        List<SseEmitter> deadEmitters = new ArrayList<>();
//        emitterSet.forEach(emitter -> {
//            try {
//                emitter.send(notification, MediaType.APPLICATION_JSON);
//
//            } catch (Exception ignore) {
//                deadEmitters.add(emitter);
//            }
//        });
//
//        emitterSet.removeAll(deadEmitters);
    }



    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePlanUpdateEvent(@NotNull PlanUpdateEvent planUpdateEvent) {
        Plan plan = participantRepository.findByUserAndPlan(planUpdateEvent.getUser(),planUpdateEvent.getPlan()).getPlan();
        log.info("plan======================", planUpdateEvent.getPlan().getPlanName());

        Optional<Notification> notification = notificationRepository.
                findByUserAndPlanAndAndNotificationType(planUpdateEvent.getUser(),planUpdateEvent.getPlan()
        ,NotificationType.UPDATE);

        if(!notification.isPresent()) {
            createNotification(plan, planUpdateEvent.getUser(), planUpdateEvent.getMessage(), NotificationType.UPDATE);
        }

    }


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePlanDeleteEvent(@NotNull PlanDeleteEvent planDeleteEvent) {
        Plan plan = participantRepository.findByUserAndPlan(planDeleteEvent.getUser(),planDeleteEvent.getPlan()).getPlan();

        Optional<Notification> notification = notificationRepository.
                findByUserAndPlanAndAndNotificationType(planDeleteEvent.getUser(),planDeleteEvent.getPlan()
                        ,NotificationType.DELETE);

        if(!notification.isPresent()) {
            createNotification(plan, planDeleteEvent.getUser(), planDeleteEvent.getMessage(), NotificationType.DELETE);
        }


        // send ( 작성자 )
        //sendToClient(emitter, key, NotificationResponse.from(notification));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleLeaveEvent(@NotNull LeaveEvent leaveEvent) {
        Plan plan = participantRepository.findByUserAndPlan(leaveEvent.getUser(),leaveEvent.getPlan()).getPlan();

        Optional<Notification> notification = notificationRepository.
                findByUserAndPlanAndAndNotificationType(leaveEvent.getUser(),leaveEvent.getPlan()
                        ,NotificationType.LEAVE);

        if(!notification.isPresent()) {
            Notification notification1 = createNotification(plan, leaveEvent.getUser(), leaveEvent.getMessage(), NotificationType.LEAVE);
            sendToClient(new SseEmitter(),String.valueOf(notification1.getId()),notification1);
        }
    }


    private Notification createNotification(Plan plan, User user, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setPlan(plan);
        notification.setUser(user);
        notification.setNotificationType(notificationType);
        notification.setMessage(message);
        notification.setRead(false);
        notificationRepository.save(notification);
        return notification;
    }

//    public void send(Optional<Notification> notification) {
//
//        String id = "";
//        if (notification.isPresent()) {
//            String id = String.valueOf(notification.);
//        }
//        // 로그인 한 유저의 SseEmitter 모두 가져오기
//        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(id);
//        sseEmitters.forEach(
//                (key, emitter) -> {
//                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
//                    emitterRepository.saveEventCache(key, notification);
//                    // 데이터 전송
//                    notificationService.sendToClient(emitter, key, NotificationResponse.from(notification));
//                }
//        );
//    }

//    public void send2(Notification notification) {
//
//        String id = String.valueOf(notification.getId());
//
//        // 로그인 한 유저의 SseEmitter 모두 가져오기
//        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(id);
//        sseEmitters.forEach(
//                (key, emitter) -> {
//                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
//                    emitterRepository.saveEventCache(key, notification);
//                    // 데이터 전송
//                    notificationService.sendToClient(emitter, key,notification);
//                }
//        );
//    }

    public void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결 오류!");
        }
    }
}
