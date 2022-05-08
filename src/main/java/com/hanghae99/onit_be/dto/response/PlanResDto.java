package com.hanghae99.onit_be.dto.response;

import com.hanghae99.onit_be.entity.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class PlanResDto implements Serializable {

    private Long planId;
    private String planName;
    private String planDate;
    private Location locationDetail;
    private int status;
    private boolean writer;
    private String url;

    public PlanResDto(Long planId, String planName, String planDateCv, Location locationDetail, int status, boolean result, String url) {
    this.planId = planId;
    this.planName = planName;
    this.planDate = planDateCv;
    this.locationDetail = locationDetail;
    this.status = status;
    this.writer = result;
    this.url = url;
    }

}
