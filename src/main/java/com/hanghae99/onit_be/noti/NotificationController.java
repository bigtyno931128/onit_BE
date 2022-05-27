//package com.hanghae99.onit_be.noti;
//
//
//import com.hanghae99.onit_be.noti.event.NotificationEvent;
//import com.hanghae99.onit_be.noti.event.NotificationEventListener;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.event.EventListener;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//@RestController
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;
//    private final NotificationEventListener notificationEventListener;
//    private Set<SseEmitter> emitterSet = new CopyOnWriteArraySet<>();
//
////    @GetMapping(value = "/notice", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
////    @ResponseStatus(HttpStatus.OK)
////    public SseEmitter signup(HttpServletRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
////        log.info("SSE stream 접근 : {}", request.getRemoteAddr());
////        log.info(userDetails.getNickName());
////        SseEmitter emitter = new SseEmitter(SSE_SESSION_TIMEOUT);
////
////        try {
////            // 연결!!
////            emitter.send(SseEmitter.event().name("connect"));
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        emitterSet.add(emitter);
////        //notificationService.notifyAddCommentEvent(userDetails.getUser());
////        emitter.onTimeout(() -> emitterSet.remove(emitter));
////        emitter.onCompletion(() -> emitterSet.remove(emitter));
////        return emitter;
////    }
//
//    @GetMapping("/event")
//
//    public SseEmitter handle() {
//
//        final SseEmitter emitter = new SseEmitter(SSE_SESSION_TIMEOUT);
//
//        this.emitterSet.add(emitter);
//
//        emitter.onCompletion( new Runnable() {
//            public void run() {
//                emitterSet.remove(emitter);
//            }
//        });
//        emitter.onTimeout( new Runnable() {
//            public void run() {
//                emitterSet.remove(emitter);
//            }
//        });
//        return emitter;
//    }
//
//    @EventListener
//    public void onIssueStateChangeEvent(NotificationEvent event) {
//        List<SseEmitter> deadEmitters = new ArrayList<>();
//        for(SseEmitter emitter : emitterSet ) {
//            try {
//                emitter.send(event.getPlan().getPlanName());
//            } catch (Exception e) {
//                deadEmitters.add(emitter);
//            }
//        }
//        this.emitterSet.removeAll(deadEmitters);
//    }
//
//}
