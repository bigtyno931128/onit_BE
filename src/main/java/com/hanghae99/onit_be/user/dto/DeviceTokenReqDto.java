package com.hanghae99.onit_be.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class DeviceTokenReqDto {
    @NotBlank
    private String token;
}

