package com.hanghae99.onit_be.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final String REDIS_CHAT_KEY = "CHATS";
    private final String REDIS_CHAT_PREFIX = "CHAT";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SocketService socketService;
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, List<ChatDto>> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }


    @MessageMapping("/enter") // maps/enter
    public void enter(@Payload EnterDto enterDto) {
        MapDto mapDto = MapDto.from(enterDto);
        socketService.setDestination(enterDto.getPlanId(), mapDto);
        simpMessagingTemplate.convertAndSend("/topic/map/" + mapDto.getPlanId(), mapDto);
    }

    @MessageMapping("/map.send") // maps/map.send
    public void sendMap(@Payload MapDto mapDto) {
        simpMessagingTemplate.convertAndSend("/topic/map/" + mapDto.getPlanId(), mapDto);
    }


    //필요 없을듯 .
//    @MessageMapping("/chat.send") // maps/chat.send
//    public void sendChat(@Payload ChatDto chatDto) {
//
//        List<ChatDto> chats = hashOperations.get(REDIS_CHAT_KEY, REDIS_CHAT_PREFIX + chatDto.getPlanId());
//        if (chats == null) {
//            chats = new ArrayList<>();
//        }
//        chats.add(chatDto);
//        hashOperations.put(REDIS_CHAT_KEY, REDIS_CHAT_PREFIX + chatDto.getPlanId(), chats);
//
//        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatDto.getPlanId(), chatDto);
//    }
}
