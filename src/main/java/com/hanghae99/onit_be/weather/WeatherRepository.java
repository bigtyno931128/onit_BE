package com.hanghae99.onit_be.weather;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface WeatherRepository extends JpaRepository<Weather, Long> {

    //Weather findByPlanDateAndPlanId(LocalDateTime planDate,Long planId);
    List<Weather> findAllByPlanId(Long planId);
}
