package com.hanghae99.onit_be.websocket.dto;

import com.hanghae99.onit_be.websocket.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatDto {
    private Long planId;
    private MessageType type;
    private String sender;
    private String content;
}
