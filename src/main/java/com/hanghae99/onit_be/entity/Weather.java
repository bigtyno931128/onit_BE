package com.hanghae99.onit_be.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_weather")
public class Weather {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "weather_id")
    private Long id;

    @Column
    private LocalDateTime planDate;

    @Column
    private LocalDate weatherDate;

    @Column
    private String address;

    @Column
    private String main;

    @Column
    private String description;

    private Long planId;

    @Column
    private int temp;

    private String icon;

    public Weather(String address, String main, String id2, int temp, LocalDateTime planDate, LocalDate weatherTime, Long planId, String icon) {
        this.planId = planId;
        this.address = address;
        this.main = main;
        this.description = id2;
        this.temp = temp;
        this.planDate = planDate;
        this.weatherDate = weatherTime;
        this.icon = icon;
    }
}
