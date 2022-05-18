//package com.hanghae99.onit_be.noti;
//
//import com.hanghae99.onit_be.entity.User;
//import com.hanghae99.onit_be.repository.NotificationRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class NotificationService {
//    private final EmitterRepository emitterRepository;
//    private final ApplicationEventPublisher applicationEventPublisher;
//    private final NotificationRepository notificationRepository;
//
//    public void notifyAddCommentEvent(User user) {
//        // 댓글에 대한 처리 후 해당 댓글이 달린 게시글의 pk값으로 게시글을 조회
//        Notification notification = notificationRepository.findByUser(user).orElseThrow(IllegalArgumentException::new);
//
//        Long userId = notification.getUser().getId();
//
//        if (sseEmitters.containsKey(userId)) {
//            SseEmitter sseEmitter = sseEmitters.get(userId);
//            try {
//                sseEmitter.send(SseEmitter.event().name("Plan").data("!!!!!"));
//            } catch (Exception e) {
//                sseEmitters.remove(userId);
//            }
//        }
//    }
//}
