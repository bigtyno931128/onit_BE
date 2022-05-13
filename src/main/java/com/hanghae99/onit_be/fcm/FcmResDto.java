package com.hanghae99.onit_be.fcm;

import com.hanghae99.onit_be.entity.Plan;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
public class FcmResDto {
    private String token;
    private String title;
    private String body;
    private String url;

    @Builder
    public FcmResDto(String token, String title, String body, String url) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.url = url;
    }

    private static String reformBody(Plan plan) {
        return String.format("모임시간 %d분 전입니다!\n%s", ChronoUnit.MINUTES.between(LocalDateTime.now(), plan.getPlanDate()), plan.getUrl());
    }

    public static FcmResDto of(Plan plan) {
        return FcmResDto.builder()
                .token(plan.getUser().getToken())
                .title("모두모여(Momo")
                .body(FcmResDto.reformBody(plan))
                .url(plan.getUrl())
                .build();
    }
}
