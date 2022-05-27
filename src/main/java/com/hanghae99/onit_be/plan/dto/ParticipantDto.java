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
        if(!participant.getUser().getProfileImg().equals("https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileImg_default.png")) {
            this.img = "https://onit-bucket.s3.ap-northeast-2.amazonaws.com/" + participant.getUser().getProfileImg();
        }else {
            this.img = "https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileImg_default.png";
        }
    }
}
