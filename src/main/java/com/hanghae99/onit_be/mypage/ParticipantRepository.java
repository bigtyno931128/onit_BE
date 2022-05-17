package com.hanghae99.onit_be.mypage;

import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findByUserAndPlan(User user, Plan plan);

    // 일정 시간에 따른 조회. (일정 가까운 순으로 정렬되어 있다.)
    List<Participant> findAllByUserOrderByPlanDate(User user);

    Optional<Participant> findByUser(User user);

    void deleteByUserAndPlan(User user, Plan plan);
}
