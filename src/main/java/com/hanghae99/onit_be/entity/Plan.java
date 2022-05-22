package com.hanghae99.onit_be.entity;

import com.hanghae99.onit_be.plan.dto.PlanReqDto;
import com.hanghae99.onit_be.weather.Weather;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_plan")
public class Plan extends TimeStamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "plan_id")
    private Long id;

    private String planName;

    private LocalDateTime planDate;

    @Embedded
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //일정을 수정,삭제 할때 권한을 작성자만 할 수 있게 식별하기 위해 컬럼 추가
    private String writer;
    //일정 추가 시, 받아온 패널티를 저장해두기 위해 컬럼 추가
    private String penalty;

    private String url;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private final List<Weather> weatherList = new ArrayList<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Participant> participantList = new ArrayList<>();


    public Plan(PlanReqDto planReqDto, User user, String url) {
        this.planName = planReqDto.getPlanName();
        this.planDate = planReqDto.getPlanDate();
        this.location = planReqDto.getLocation();
        this.writer = user.getNickname();
        this.penalty = planReqDto.getPenalty();
        this.url = url;
    }

    public Plan(Plan planNew, User user1) {
        this.planName = planNew.getPlanName();
        this.planDate = planNew.getPlanDate();
        this.penalty = planNew.getPenalty();
        this.url = planNew.getUrl();
        this.writer = planNew.getWriter();
        this.location = planNew.getLocation();
        addPlan(user1);
    }

    public void update(PlanReqDto planReqDto) {
        this.planName = planReqDto.getPlanName();
        this.planDate = planReqDto.getPlanDate();
        this.location = planReqDto.getLocation();
        this.writer = user.getNickname();
        this.penalty = planReqDto.getPenalty();
    }

    public void addPlan(User user) {
        this.user = user;
        user.getPlanList().add(this);
    }

    public void updateSave(Plan plan) {
        this.planName = plan.getPlanName();
        this.planDate = plan.getPlanDate();
        this.url =plan.getUrl();
        this.user = plan.getUser();
        this.writer = plan.getWriter();
        this.penalty = plan.getPenalty();
        this.location = plan.getLocation();
    }
}
