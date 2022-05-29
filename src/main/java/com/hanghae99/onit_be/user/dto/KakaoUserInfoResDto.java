package com.hanghae99.onit_be.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class KakaoUserInfoResDto {

    private Long id;
    private String nickname;
    private String profileImg;

}