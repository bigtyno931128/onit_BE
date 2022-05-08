package com.hanghae99.onit_be.dto.response;

import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Plan;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PlanResDto implements Serializable {

    private Long planId;
    private String planName;
    private LocalDateTime planDate;
    private Location locationDetail;
    private int status;
    private boolean writer;
    private String url;

    public PlanResDto(Long planId, String planName, LocalDateTime planDate, Location locationDetail, int status, boolean result, String url) {
    this.planId = planId;
    this.planName = planName;
    this.planDate = planDate;
    this.locationDetail = locationDetail;
    this.status = status;
    this.writer = result;
    this.url = url;
    }

    public PlanResDto(Long planId, String planName, LocalDateTime planDate) {
        this.planId = planId;
        this.planName = planName;
        this.planDate = planDate;
    }
}
