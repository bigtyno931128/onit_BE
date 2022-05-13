package com.hanghae99.onit_be.dto.response;

import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Participant;
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
    private String url;
    private boolean isMember;


    public PlanDetailResDto(Plan plan) {
        // planId는 url을 위해 필요?
        this.planId = plan.getId();
        this.planName = plan.getPlanName();
        this.planDate = plan.getPlanDate();
        this.writer = plan.getWriter();
        this.locationDetail = plan.getLocation();
        this.penalty = plan.getPenalty();
        this.url = plan.getUrl();
    }

    public PlanDetailResDto(Participant participant) {
        this.planId = participant.getPlan().getId();
        this.planName = participant.getPlan().getPlanName();
        this.planDate = participant.getPlan().getPlanDate();
        this.writer = participant.getPlan().getWriter();
        this.locationDetail = participant.getPlan().getLocation();
        this.penalty = participant.getPlan().getPenalty();

    }

    public PlanDetailResDto(Plan plan, boolean isMember) {
        this.planId = plan.getId();
        this.planName = plan.getPlanName();
        this.planDate = plan.getPlanDate();
        this.writer = plan.getWriter();
        this.locationDetail = plan.getLocation();
        this.penalty = plan.getPenalty();
        this.url = plan.getUrl();
        this.isMember = isMember;
    }
}
