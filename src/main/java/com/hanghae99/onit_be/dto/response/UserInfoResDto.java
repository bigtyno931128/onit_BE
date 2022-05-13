package com.hanghae99.onit_be.dto.response;

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

    public UserInfoResDto(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profileImg = user.getProfileImg();
    }
}
