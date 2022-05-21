package com.hanghae99.onit_be.websocket;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@Component
@Slf4j
// Session의 Connect / Disconnect 되는 시점을 알고 싶을 때 사용하는 구현체
public class ChannelInterceptorImpl implements ChannelInterceptor {

    @Override
    public void postSend(@NotNull Message<?> message, @NotNull MessageChannel channel, boolean sent) {
        // StompHeaderAccessor.wrap으로 message를 감싸면 STOMP의 헤더에 직접 접근 가능
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // connect/disconnect 시도할 때 로그인 확인여부?
        StompCommand command = accessor.getCommand();
        log.info("{}", message);

        // 로그인 정보가 없거나 disconnect인 상태라면
        // 사용자의 위도,경도,시간을 redis에 저장해서 계속 전달 !
        if (command != null && command.equals(StompCommand.DISCONNECT)) {
            // 해당 사용자의 세션Id를 가져오기
            String sessionId = accessor.getSessionId();
            // 세션 Id가 있다면
            if (sessionId != null) {
                // planId는 사용자의 sessionId가 됨 ???
                String planId = (String) accessor.getHeader(sessionId);
                log.info(planId);
            }
            log.info(sessionId + "가 나갔습니다");
        }
    }
}