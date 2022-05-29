package com.hanghae99.onit_be.noti;

import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;



    public List<NoticeDto> getNoticeList(User user) {

        List<Notification> notificationList = notificationRepository.findAllByUser(user);
        List<NoticeDto> noticeDtos = new ArrayList<>();

        for (Notification notification : notificationList ) {

            Long id = notification.getId();
            String title = notification.getPlan().getPlanName();
            String message = notification.getMessage();
            String url = "https://imonit.co.kr/detail/" + notification.getPlan().getUrl();
            String userName = user.getNickname();
            notification.setRead(true);
            boolean isRead = true;
            NoticeDto noticeDto = new NoticeDto(id,title,message,url,userName,isRead);
            noticeDtos.add(noticeDto);
        }
        return noticeDtos;
    }



//    public SseEmitter subscribe(Long userId) {
//        // 1
//        String id = userId + "_" + System.currentTimeMillis();
//
//        // 2
//        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));
//
//        emitter.onCompletion(() -> emitterRepository.deleteById(id));
//        emitter.onTimeout(() -> emitterRepository.deleteById(id));
//
//        // 3
//        // 503 에러를 방지하기 위한 더미 이벤트 전송
//        sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");
//        Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
//        // 4
//        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
////        if (!lastEventId.isEmpty()) {
////
////
////            events.entrySet().stream()
////                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
////                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
////        }
//
//        return emitter;
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



