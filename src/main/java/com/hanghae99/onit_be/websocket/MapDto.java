package com.hanghae99.onit_be.websocket;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class MapDto {
    private Long planId;
    private String sender;
    private String profileImg;
    private String lat;
    private String lng;
    private MessageType type;
    private String destLat;
    private String destLng;

    @Builder
    public MapDto(Long planId, String sender, String lat, String lng, String destLat, String destLng, MessageType type, String profileImg) {
        this.planId = planId;
        this.sender = sender;
        this.lat = lat;
        this.lng = lng;
        this.destLat = destLat;
        this.destLng = destLng;
        this.type = type;
        this.profileImg = profileImg;
    }


    public static MapDto from(EnterDto enterDto,String profileImg) {
        return MapDto.builder()
                .planId(enterDto.getPlanId())
                .sender(enterDto.getSender())
                .profileImg((profileImg))
                .lat(enterDto.getLat())
                .lng(enterDto.getLng())
                .type(MessageType.DEST)
                .build();
    }
}
