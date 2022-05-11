package com.hanghae99.onit_be.fcm;


import com.hanghae99.onit_be.entity.Plan;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
public class FcmResponseDto {
    private String token;
    private String title;
    private String body;
    private String url;

    @Builder
    public FcmResponseDto(String token, String title, String body, String url) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.url = url;
    }

    // %s 대상: 모든 자료형. -> 반환: 문자열 값. 시간 차이를 계산 , 1시간전으로 할거면 ChronoUnit 에 다른 메서드 이용.
    private static String reformBody(Plan plan) {
        return String.format("약속시간 %d분 전입니다!\n%s",
                ChronoUnit.MINUTES.between(LocalDateTime.now(), plan.getPlanDate()), plan.getUrl());
    }


    //2 ) Plan 으로 부터 FcmResponseDto 를 만든다 . token 과 title ,
    public static FcmResponseDto of(Plan plan) {
        return FcmResponseDto.builder()
                .token(plan.getUser().getToken())
                .title("(Onit)너 지금 오고 있니?")
                .body(FcmResponseDto.reformBody(plan))
                .url(plan.getUrl())
                .build();
    }
}

