package com.hanghae99.onit_be.weather;


import com.hanghae99.onit_be.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface WeatherRepository extends JpaRepository<Weather, Long> {

    //Weather findByPlanDateAndPlanId(LocalDateTime planDate,Long planId);
    List<Weather> findAllByPlanId(Long planId);
    void deleteAllByPlanId(Long id);
    Optional<Weather> findByWeatherDate(LocalDate weatherDate);
    Weather findByWeatherDateAndPlanId(LocalDate weatherDate,Long planId);
}
