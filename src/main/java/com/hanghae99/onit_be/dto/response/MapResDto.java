package com.hanghae99.onit_be.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapResDto {
    private Long planId;
    private String planName;

    public MapResDto(String planName, Long id) {
        this.planId = id;
        this.planName = planName;
    }
}
