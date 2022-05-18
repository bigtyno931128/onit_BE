package com.hanghae99.onit_be.websocket.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalTime;

@Getter
@Setter
@RedisHash("MapLeaveDto")
public class MapLeaveDto {

    @Id
    private String id;
    private Double lat;
    private Double lng;
    private LocalTime leaveTime;

}
