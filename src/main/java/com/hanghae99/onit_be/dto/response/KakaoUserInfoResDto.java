package com.hanghae99.onit_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class KakaoUserInfoResDto {
    private Long id;
    private String nickname;
//    private String thumbnailUrl;
}