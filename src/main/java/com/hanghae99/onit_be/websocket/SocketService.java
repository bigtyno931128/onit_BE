package com.hanghae99.onit_be.websocket;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.plan.PlanRepository;
import com.hanghae99.onit_be.websocket.dto.MapDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

import static com.hanghae99.onit_be.common.utils.Valid.distance;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketService {

    private final PlanRepository planRepository;

    public void setDestination(Long planId, MapDto mapDto) {

        Plan plan = planRepository.findById(planId).orElseThrow(IllegalArgumentException::new);
        mapDto.setDestLat(String.valueOf(plan.getLocation().getLat()));
        mapDto.setDestLng(String.valueOf(plan.getLocation().getLng()));

    }

    public void setDistance(MapDto mapDto) {

        Plan plan = planRepository.findById(mapDto.getPlanId()).orElseThrow(IllegalArgumentException::new);
        mapDto.setDestLat(String.valueOf(plan.getLocation().getLat()));
        mapDto.setDestLng(String.valueOf(plan.getLocation().getLng()));
        //mapDto.setProfileImg(plan.getUser().getProfileImg());
        double a = Double.parseDouble(mapDto.getLat());
        double b = Double.parseDouble(mapDto.getLng());
        double c = plan.getLocation().getLat();
        double d = plan.getLocation().getLng();

        double distance = distance(a, b, c, d, "kilometer");
        int point = (int) Math.ceil(distance);
        log.info("목적지 까지의 거리 =={}", distance);

        //여기서 부터 거리에 따른 메세지 전달 .
        String distnace = "가는중";
        log.info("거리=={}", point);
        if (1 >= point) {
            distnace = "도착";
            log.info(distnace);
        }
        mapDto.setDistance(distnace);
    }
}
