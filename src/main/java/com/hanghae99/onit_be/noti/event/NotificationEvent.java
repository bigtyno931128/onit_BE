package com.hanghae99.onit_be.noti.event;

import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import lombok.Getter;


@Getter
public class NotificationEvent {

    private final Plan plan;
    private final User user;

    //일정에 참여.
    public NotificationEvent(Participant participant) {
        this.plan = participant.getPlan();
        this.user = participant.getUser();
    }

}
