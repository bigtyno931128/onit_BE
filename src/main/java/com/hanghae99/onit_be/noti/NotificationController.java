package com.hanghae99.onit_be.noti;


import com.hanghae99.onit_be.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;
    private Set<SseEmitter> emitterSet = new CopyOnWriteArraySet<>();
    private final NotificationService notificationService;


    @GetMapping(value = "/notice", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter notice(HttpServletRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("SSE stream 접근 : {}", request.getRemoteAddr());

        SseEmitter emitter = new SseEmitter(SSE_SESSION_TIMEOUT);

        emitterSet.add(emitter);

        emitter.onTimeout(() -> emitterSet.remove(emitter));
        emitter.onCompletion(() -> emitterSet.remove(emitter));

        return emitter;
    }


    @GetMapping(value = "/notices")
    public List<NoticeDto> getNoticeList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getNoticeList(userDetails.getUser());
    }

}
