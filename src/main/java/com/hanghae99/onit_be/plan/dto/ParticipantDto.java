package com.hanghae99.onit_be.plan.dto;

import com.hanghae99.onit_be.entity.Participant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantDto {

    private Long id;
    private String nickName;
    private String img;


    public ParticipantDto (Participant participant ) {
        this.id = participant.getUser().getId();
        this.nickName = participant.getUser().getNickname();
        this.img = participant.getUser().getProfileImg();
    }
}
