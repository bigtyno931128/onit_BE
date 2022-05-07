package com.hanghae99.onit_be.utils;

import com.hanghae99.onit_be.dto.request.PlanReqDto;
import com.hanghae99.onit_be.entity.Plan;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class Date {

    // 날짜 비교
    public static int compareDay (LocalDateTime date1, LocalDateTime date2) {
        LocalDateTime dayDate1 = date1.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime dayDate2 = date2.truncatedTo(ChronoUnit.DAYS);
        return dayDate1.compareTo(dayDate2);
    }
    
    // 시간 가져오는 메서드
    public static long getHours(PlanReqDto planReqDto, Plan plans) {
        return ChronoUnit.HOURS.between(plans.getPlanDate().toLocalTime(), planReqDto.getPlanDate().toLocalTime());
    }
    
    //일정약속이 현재,미래,과거인지 판별 하는 메서드 ex) 1 미래 , -1 과거 , 0 현재
    public static int getStatus(int status, LocalDateTime planDate) {
        if (LocalDateTime.now(ZoneId.of("Asia/Seoul")).isBefore(planDate)){
            status = 1;
        }
        // 과거의 약속 (현재 서울 날짜의 시간이 planDate보다 이후일 때)
        if (LocalDateTime.now(ZoneId.of("Asia/Seoul")).isAfter(planDate)){
            status = -1;
        }
        // 현재의 약속 (현재 서울 날짜의 시간이 planDate와 같을 때)
        if (LocalDate.now(ZoneId.of("Asia/Seoul")).isEqual(ChronoLocalDate.from(planDate))){
            status = 0;
        }
        return status;
    }
}

