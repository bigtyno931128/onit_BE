package com.hanghae99.onit_be.repository;

import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findByUserAndPlan(User user, Plan plan);
    List<Participant> findAllByUser(User user);


    Optional<Participant> findByUser(User user);

    void deleteByUserAndPlan(User user, Plan plan);
}
