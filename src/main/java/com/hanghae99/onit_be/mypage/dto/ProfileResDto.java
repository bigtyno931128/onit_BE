package com.hanghae99.onit_be.mypage.dto;

import com.hanghae99.onit_be.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileResDto {
    private Long userId;
    private String profileImg;

    public ProfileResDto(User profile) {
        this.userId = profile.getId();
        this.profileImg = profile.getProfileImg();
    }
}
