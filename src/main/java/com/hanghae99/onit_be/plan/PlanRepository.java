package com.hanghae99.onit_be.plan;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findAllByUserOrderByPlanDateAsc(User user);
    Optional<Plan> findPlanByUrl(String url);
    List<Plan> findAllByUrl(String url);
    Plan findByUrl(String url);
    Plan findByUser(User user);
    void deleteByUrl(String url);

    List<Plan> findAllByUserOrderByPlanDateDesc(User user);
    //Plan getByUser(User orElseThrow);
    List<Plan> findAllByPlanDateBetween(LocalDateTime tommorrow, LocalDateTime today);
}
