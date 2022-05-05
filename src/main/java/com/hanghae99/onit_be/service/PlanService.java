package com.hanghae99.onit_be.service;

import com.hanghae99.onit_be.dto.request.PlanReqDto;
import com.hanghae99.onit_be.dto.response.PlanDetailResDto;
import com.hanghae99.onit_be.dto.response.PlanResDto;
import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.repository.PlanRepository;
import com.hanghae99.onit_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    // 일정 생성
    @Transactional
    public void createPlan(PlanReqDto planReqDto, User user) {
        // 이중 약속 유효성 검사
        // 1. 로그인한 유저의 닉네임으로 저장된 모든 plan list 조회
        List<Plan> planList = planRepository.findAllByWriter(user.getNickname());
        LocalDateTime today = planReqDto.getPlanDate();
        for (Plan plans : planList) {
            // 2. 이중 약속에 대한 처리 (약속 날짜와 오늘 날짜 비교)
            int comResult = compareDay(plans.getPlanDate(), today);
            if (comResult == 0) {
                // 3. 약속 시간 기준 +-2에 해당하는 약속은 정할 수 없게 처리
                // ex) 6시에 일정이 있으면 > 4시부터 8시 사이에는 일정을 잡지 못함
                long remainHours = ChronoUnit.HOURS.between(plans.getPlanDate().toLocalTime(),planReqDto.getPlanDate().toLocalTime());
                if (!(remainHours > 2 || remainHours < -2))
                    throw new IllegalArgumentException("오늘 일정은 이미 있습니다.");
            }
        }
        Plan plan = new Plan(planReqDto, user);
        planRepository.save(plan);
    }

    // Day 비교
    public static int compareDay (LocalDateTime date1, LocalDateTime date2) {
        LocalDateTime dayDate1 = date1.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime dayDate2 = date2.truncatedTo(ChronoUnit.DAYS);
        return dayDate1.compareTo(dayDate2);
    }

    // 일정 목록 조회
    public Page<PlanResDto> getPlanList(Long user_id, int pageno){
        List<Plan> planList = planRepository.findAllByUserOrderByPlanDateAsc(userRepository.findById(user_id).orElseThrow(IllegalArgumentException::new));
        Pageable pageable = getPageable(pageno);
        List<PlanResDto> planResDtoList = new ArrayList<>();
        // 일정 시간 비교 메서드
        forPlanList(planList, planResDtoList);

        int start = pageno * 5;
        int end = Math.min((start + 5), planList.size());

        Page<PlanResDto> page = new PageImpl<>(planResDtoList.subList(start, end),pageable,planResDtoList.size());
        return page;
    }

    // 페이지 정렬 메서드
    private Pageable getPageable(int page) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");
        return PageRequest.of(page, 5, sort);
    }

    // 일정 리스트 만드는 메서드 > status를 통해 과거,현재,미래에 대한 일정 구분
    private void forPlanList(List<Plan> planList, List<PlanResDto> planResDtoList){
        for(Plan plan : planList){
            int status = 0;
            LocalDateTime planDate = plan.getPlanDate();
            // 미래의 약속 (현재 서울 날짜의 시간이 planDate보다 이전일 때)
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

            Long planId = plan.getId();
            String planName = plan.getPlanName();
            Location locationDetail = plan.getLocation();

            PlanResDto planResDto = new PlanResDto(planId,planName,planDate,locationDetail,status);
            planResDtoList.add(planResDto);
        }
    }

    // 일정 상세 조회
    public PlanDetailResDto getPlan(Long planId){
        Plan plan = planRepository.findById(planId).orElseThrow(IllegalArgumentException::new);
        return new PlanDetailResDto(plan);
    }

    // 일정 수정
    // 일정 수정시에도 이중 약속에 대한 유효처리가 필요하지 않나 싶네요.
    @Transactional
    public void editPlan(Long planId, PlanReqDto planReqDto, User user) {
        Plan plan = planRepository.findById(planId).orElseThrow(IllegalArgumentException::new);
        plan.update(planReqDto, user);
    }

    // 일정 삭제
    public void deletePlan(Long planId){
        planRepository.deleteById(planId);
    }

}
