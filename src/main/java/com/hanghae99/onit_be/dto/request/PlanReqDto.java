package com.hanghae99.onit_be.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae99.onit_be.entity.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PlanReqDto {

    private String planName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime planDate;
    private Location location;
    private String penalty;

}
