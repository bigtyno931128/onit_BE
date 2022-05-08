package com.hanghae99.onit_be.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tbl_participant")
public class Participant {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    public Participant(Plan planNew, User user1) {
        this.plan = planNew;
        this.user = user1;
    }
}
