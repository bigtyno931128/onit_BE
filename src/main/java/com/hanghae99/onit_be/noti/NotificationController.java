package com.hanghae99.onit_be.noti;


import com.hanghae99.onit_be.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

//    private static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;
//    private Set<SseEmitter> emitterSet = new CopyOnWriteArraySet<>();
    private final NotificationService notificationService;
//
//
//    @GetMapping(value = "/notice", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter notice(HttpServletRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//
//        log.info("SSE stream 접근 : {}", request.getRemoteAddr());
//
//        SseEmitter emitter = new SseEmitter(SSE_SESSION_TIMEOUT);
//
//        emitterSet.add(emitter);
//
//        emitter.onTimeout(() -> emitterSet.remove(emitter));
//        emitter.onCompletion(() -> emitterSet.remove(emitter));
//
//        return emitter;
//    }
//
//
    @GetMapping(value = "/notices")
    public List<NoticeDto> getNoticeList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getNoticeList(userDetails.getUser());
    }


    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();


    @CrossOrigin
    @GetMapping(value = "/sub", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 토큰에서 user의 pk값 파싱
        Long userId = userDetails.getUser().getId();

        // 현재 클라이언트를 위한 SseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        log.info("연결 1번 ",sseEmitter);
        try {
            // 연결!!
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user의 pk값을 key값으로 해서 SseEmitter를 저장
        sseEmitters.put(userId, sseEmitter);
        log.info("연결 2번 ",sseEmitters.get(1));

        sseEmitter.onCompletion(() -> sseEmitters.remove(userId));
        sseEmitter.onTimeout(() -> sseEmitters.remove(userId));
        sseEmitter.onError((e) -> sseEmitters.remove(userId));

        return sseEmitter;
    }

}
