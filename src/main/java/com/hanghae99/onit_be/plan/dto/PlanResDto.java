package com.hanghae99.onit_be.plan.dto;

import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PlanResDto {

    private Long planId;
    private String planName;
    private LocalDateTime planDate;
    private Location locationDetail;
    private int status;
    private boolean isMember;
    private String url;
    private String penalty;
    private String writer;
    private String description;


    public PlanResDto(Long planId, String planName, LocalDateTime planDate) {
        this.planId = planId;
        this.planName = planName;
        this.planDate = planDate;
    }

    public PlanResDto(Long planId, String planName, LocalDateTime planDate, Location locationDetail, int status, String url, String penalty, String writer, boolean isMember) {
        this.planId = planId;
        this.planName = planName;
        this.planDate = planDate;
        this.locationDetail = locationDetail;
        this.status = status;
        this.writer = writer;
        this.isMember = isMember;
        this.url = url;
        this.penalty = penalty;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class MyPlanDto {
        private Long planId;
        private String planName;
        private String planDate;
        private String address;
        private String url;
        private String penalty;
        private int status;
        private String description;

        public MyPlanDto(Long planId, String planName, String planDateCv, String address, String url, int status, String description) {
            this.planId = planId;
            this.planName = planName;
            this.planDate = planDateCv;
            this.address = address;
            this.url = url;
            this.status = status;
            this.description = description;
        }
    }
}
