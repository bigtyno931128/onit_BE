package com.hanghae99.onit_be.repository;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findAllByUserOrderByPlanDateAsc(User user);
    Optional<Plan> findPlanByUrl(String url);
    List<Plan> findAllByUrl(String url);
    //Plan getByUser(User orElseThrow);
    List<Plan> findAllByPlanDateBetween(LocalDateTime tommorrow, LocalDateTime today);
}
