package com.hanghae99.onit_be.weather;

import com.hanghae99.onit_be.entity.Plan;
import lombok.Getter;

@Getter
public class WeatherUpdateEvent {

    private final Plan plan;

    public WeatherUpdateEvent(Plan plan) {
        this.plan = plan;
    }
}
