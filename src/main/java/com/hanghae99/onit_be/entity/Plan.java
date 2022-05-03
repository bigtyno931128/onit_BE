package com.hanghae99.onit_be.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.stream.Location;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tbl_plan")
public class Plan extends TimeStamped{

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

//    @OneToMany
//    @JoinColumn
//    private List<Guest> guests;
}
