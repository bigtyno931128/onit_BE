package com.hanghae99.onit_be.dto.response;

import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Plan;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PlanDetailResDto {
    private Long planId;
    private String planName;
    private LocalDateTime planDate;
    private String writer;
    private Location locationDetail;
    private String penalty;

    public PlanDetailResDto(Plan plan) {
        // planId는 url을 위해 필요?
        this.planId = plan.getId();
        this.planName = plan.getPlanName();
        this.planDate = plan.getPlanDate();
        this.writer = plan.getWriter();
        this.locationDetail = plan.getLocation();
        this.penalty = plan.getPenalty();
    }
}
