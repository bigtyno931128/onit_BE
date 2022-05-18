package com.hanghae99.onit_be.weather;

import com.hanghae99.onit_be.entity.Plan;
import lombok.Getter;

@Getter
public class WeatherCreateEvent {

    private final Plan plan;

    public WeatherCreateEvent(Plan plan) {
        this.plan = plan;
    }
}
