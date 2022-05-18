package com.hanghae99.onit_be.weather;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WeatherDto   {

    private LocalDateTime planDate;

    private LocalDateTime weatherDate;

    private String address;

    private String main;

    private String description;

    private Long planId;

    private int temp;

    private String icon;

}
