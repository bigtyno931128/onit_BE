package com.hanghae99.onit_be.websocket;

import com.hanghae99.onit_be.user.UserRepository;
import com.hanghae99.onit_be.websocket.dto.ChatDto;
import com.hanghae99.onit_be.websocket.dto.ChatEnterDto;
import com.hanghae99.onit_be.websocket.dto.EnterDto;
import com.hanghae99.onit_be.websocket.dto.MapDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final UserRepository userRepository;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }


    @MessageMapping("/enter") // maps/enter
    // stompHeaderAccessor를 통해 세션 데이터 조회
    public void enter(@Payload EnterDto enterDto) {
//        ChatEnterDto chatDto = ChatEnterDto.from(enterDto);
//        chatDto.setContent(chatDto.getSender() + "님이 입장하셨습니다");

        // attributes에 사용자의 세션ID-플랜Id 저장후 다시 헤더에 담아줌?
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put(headerAccessor.getSessionId(), enterDto.getPlanId());
//        headerAccessor.setSessionAttributes(attributes);

        // 대화 내용 내역들 redis에서 조회 후, dto에 담아줌
        // redis에서 정보 꺼내오는 코드
//        List<ChatDto> chats = (List<ChatDto>) redisTemplate.opsForValue().get(REDIS_CHAT_PREFIX + enterDto.getPlanId());
//        chatDto.setChats(chats);

        // 참여자의 이미지 찾아오기 .
        String profileImg = userRepository.findByNickname(enterDto.getSender()).orElseThrow(IllegalArgumentException::new).getProfileImg();

        MapDto mapDto = MapDto.from(enterDto, profileImg);
        log.info("map1====="+mapDto.getDestLat());
        socketService.setDestination(enterDto.getPlanId(), mapDto);
        log.info("map2====="+mapDto.getDestLat());

        simpMessagingTemplate.convertAndSend("/topic/map/" + mapDto.getPlanId(), mapDto);

        // 플랜 Id와 채팅 내역이 담긴 chatDto 보내기
//        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatDto.getPlanId(), chatDto);


    }

    @MessageMapping("/map.send") // maps/map.send
    public void sendMap(@Payload MapDto mapDto) {

        socketService.setDistance(mapDto);

        log.info("map.send====="+mapDto.getDestLat());


        simpMessagingTemplate.convertAndSend("/topic/map/" + mapDto.getPlanId(), mapDto);

    }


//    @MessageMapping("/chat.send") // maps/chat.send
//    public void sendChat(@Payload ChatDto chatDto) {
//        // 플랜 Id에 대한 채팅내역을 redis에서 불러오기
//        List<ChatDto> chats = hashOperations.get(REDIS_CHAT_KEY, REDIS_CHAT_PREFIX + chatDto.getPlanId());
//        if (chats == null) {
//            chats = new ArrayList<>();
//        }
//        // 기존 채팅내역이 없으면 리스트에 파라미터값을 저장
//        chats.add(chatDto);
//        // redis에도 저장
//        hashOperations.put(REDIS_CHAT_KEY, REDIS_CHAT_PREFIX + chatDto.getPlanId(), chats);
//
//        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatDto.getPlanId(), chatDto);
//    }
}
