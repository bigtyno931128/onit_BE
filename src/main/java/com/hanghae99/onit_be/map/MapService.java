package com.hanghae99.onit_be.map;

import com.hanghae99.onit_be.map.dto.MapResDto;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MapService {

    private final PlanRepository planRepository;

    public MapResDto getPlanInfo(String url) {
        Plan plan = planRepository.findPlanByUrl(url).
                orElseThrow(() -> new IllegalArgumentException("님이 찾는 일정 없음"));

        if (isPlanEnd(plan)) throw new IllegalArgumentException("유효하지 않는 링크임다");

        return new MapResDto(plan.getLocation().getName(), plan.getId());
    }

    private boolean isPlanEnd(Plan plan) {
        return plan.getPlanDate().plusHours(1L).isBefore(LocalDateTime.now());
    }
}
