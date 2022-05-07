package com.hanghae99.onit_be.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResDto {
    private Long userId;
    private String username;
    private String nickname;
    private String profileImg;

}
