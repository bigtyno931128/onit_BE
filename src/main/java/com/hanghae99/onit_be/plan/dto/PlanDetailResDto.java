package com.hanghae99.onit_be.plan.dto;

import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    // 참여 여부
    private boolean isMember;
    // 날씨
    private String description;

    // 참여자 목록 ( id , 닉네임 , 이미지 )
    private List<ParticipantDto> participantList;


    public PlanDetailResDto(Plan plan) {
        // planId는 url을 위해 필요?
        this.planId = plan.getId();
        this.planName = plan.getPlanName();
        this.planDate = plan.getPlanDate();
        this.writer = plan.getWriter();
        this.locationDetail = plan.getLocation();
        this.penalty = plan.getPenalty();
        this.url = plan.getUrl();
        this.isMember = plan.isMember();
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

    public PlanDetailResDto(Plan plan, boolean isMember, List<ParticipantDto> participantDtoList) {
        this.planId = plan.getId();
        this.planName = plan.getPlanName();
        this.planDate = plan.getPlanDate();
        this.writer = plan.getWriter();
        this.locationDetail = plan.getLocation();
        this.penalty = plan.getPenalty();
        this.url = plan.getUrl();
        this.isMember = isMember;
        this.participantList = participantDtoList;

    }
}
