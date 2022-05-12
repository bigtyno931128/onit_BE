package com.hanghae99.onit_be.fcm;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestDTO {

    private String targetToken;
    private String title;
    private String body;
}
