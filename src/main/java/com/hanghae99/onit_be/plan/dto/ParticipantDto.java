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

        if (!participant.getUser().getProfileImg().equals("https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileM1.png")) {
            img = "https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileM1.png";
        } else if (!participant.getUser().getProfileImg().equals("https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileM2.png")){
            img = "https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileM2.png";

        } else if (!participant.getUser().getProfileImg().equals("https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileW1.png")){
            img = "https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileW1.png";
        } else if (!participant.getUser().getProfileImg().equals("https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileW2.png")) {
            img = "https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileW2.png";
        } else {
            img = "https://onit-bucket.s3.ap-northeast-2.amazonaws.com/" + participant.getUser().getProfileImg();
        }
    }
}
