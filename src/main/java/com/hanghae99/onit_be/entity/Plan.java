package com.hanghae99.onit_be.entity;

import com.hanghae99.onit_be.dto.request.PlanReqDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_plan")
public class Plan extends TimeStamped implements Serializable {

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


    public Plan(PlanReqDto planReqDto, User user) {
        this.planName = planReqDto.getPlanName();
        this.planDate = planReqDto.getPlanDate();
        this.user = user;
        this.location = planReqDto.getLocation();
        this.writer = user.getNickname();
        this.penalty = planReqDto.getPenalty();
    }

    public void update(PlanReqDto planReqDto, User user) {
        this.planName = planReqDto.getPlanName();
        this.planDate = planReqDto.getPlanDate();
        this.user = user;
        this.location = planReqDto.getLocation();
        this.writer = user.getNickname();
        this.penalty = planReqDto.getPenalty();
    }

//    @OneToMany
//    @JoinColumn
//    private List<Guest> guests;
}
