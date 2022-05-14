package com.hanghae99.onit_be.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapResDto {
    private Long planId;
    private String locationName;


    public MapResDto(String name, Long id) {
        this.planId = id;
        this.locationName = name;
    }
}
