package com.hanghae99.onit_be.user.dto;

import com.hanghae99.onit_be.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResDto {
    private Long userId;
//    private String username;
    private String nickname;
    private String profileImg;

    public UserInfoResDto(User user, String profile) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profileImg = profile;
    }
}
