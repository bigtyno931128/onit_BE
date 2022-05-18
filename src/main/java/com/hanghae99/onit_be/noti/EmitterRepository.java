package com.hanghae99.onit_be.noti;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    //Emitter 저장
    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    // 이벤트 저장
    void saveEventCache(String emitterId, Object event);
    //해당 회원과 관련된 모든 Emitter를 찾는다
    Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId);
    //해당 회원과 관련된 모든 이벤트를 찾는다.
    Map<String, Object> findAllEventCacheStartWithByUserId(String userId);
    //Emitter를 지운다
    void deleteById(String id);
    //해당 회원과 관련된 모든 Emitter를 지운다.
    void deleteAllEmitterStartWithId(String userId);
    //해당 회원과 관련된 모든 이벤트를 지운다.
    void deleteAllEventCacheStartWithId(String userId);
}
