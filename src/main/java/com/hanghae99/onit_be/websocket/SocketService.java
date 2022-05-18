package com.hanghae99.onit_be.websocket;

import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.plan.PlanRepository;
import com.hanghae99.onit_be.websocket.dto.MapDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketService {

    private final PlanRepository planRepository;

    public void setDestination(Long planId, MapDto mapDto) {
        Plan plan = planRepository.findById(planId).orElseThrow(IllegalArgumentException::new);
        mapDto.setDestLat(String.valueOf(plan.getLocation().getLat()));
        mapDto.setDestLng(String.valueOf(plan.getLocation().getLng()));
        //mapDto.setProfileImg(plan.getUser().getProfileImg());
        double a = Double.parseDouble(mapDto.getLat());
        double b = Double.parseDouble(mapDto.getLng());
        double c = plan.getLocation().getLat();
        double d = plan.getLocation().getLng();

        double distance = geoDistance(a,b,c,d);

        log.info("목적지 까지의 거리 =={}", distance);

        //여기서 부터 거리에 따른 메세지 전달 .
        String status = "";
    }

    /**
     * 두 좌표 거리 구하기
     * @param latitude1 Start latitude
     * @param longitude1 Start longitude
     * @param latitude2 End latitude
     * @param longitude2 End longitude
     * @return Distance(m)
     */
    public static double geoDistance(double latitude1, double longitude1, double latitude2, double longitude2) {

        DecimalFormat df = new DecimalFormat("#.#####");

        if ((latitude1 == latitude2) && (longitude1 == longitude2)) {
            return 0;
        } else {
            double theta = longitude1 - longitude2;
            double distance = Math.sin(Math.toRadians(latitude1)) * Math.sin(Math.toRadians(latitude2)) +
                    Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * Math.cos(Math.toRadians(theta));
            distance = Math.acos(distance);
            distance = Math.toDegrees(distance);
            distance = distance * 60 * 1.1515;
            distance = distance * 1.609344;

            //distance --> 0보다 클 경우
            if (0 < distance) {
                distance = Double.valueOf(df.format(distance));    //소숫점 다섯째 자리에서 반올림
                // distance --> 0일 경우
            } else {
                distance = 0;
            }
            distance = distance * 1000; //km --> m으로 변환
            return distance;
        }
    }
}
