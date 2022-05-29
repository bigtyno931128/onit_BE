package com.hanghae99.onit_be.noti.event;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParticipateEvent {

    private final Plan plan;
    private final String message;
    private final User user;
}
