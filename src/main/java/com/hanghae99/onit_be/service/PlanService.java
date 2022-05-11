package com.hanghae99.onit_be.service;

import com.hanghae99.onit_be.dto.request.PlanReqDto;
import com.hanghae99.onit_be.dto.response.PlanDetailResDto;
import com.hanghae99.onit_be.dto.response.PlanResDto;
import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.noti.event.PlanCreateEvent;
import com.hanghae99.onit_be.repository.ParticipantRepository;
import com.hanghae99.onit_be.repository.PlanRepository;
import com.hanghae99.onit_be.repository.UserRepository;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.hanghae99.onit_be.utils.Date.*;
import static com.hanghae99.onit_be.utils.Page.getPageable;
import static com.hanghae99.onit_be.utils.Valid.validWriter;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ApplicationEventPublisher eventPublisher;


    // 일정 생성
    @Transactional
    public void createPlan(PlanReqDto planReqDto, User user) {

        //과거 이면 등록 x
        checkPlanDate(planReqDto);

        User user1 = userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new);
        // 이중 약속 유효성 검사
        // 1. 로그인한 유저의 닉네임으로 저장된 모든 plan list 조회
        List<Participant> participantList = participantRepository.findAllByUser(user1);
        List<Plan> planList = new ArrayList<>();
        for(Participant participant : participantList){
            Plan plan = participant.getPlan();
            planList.add(plan);
        }

        LocalDateTime today = planReqDto.getPlanDate();
        for (Plan plans : planList) {
            // 2. 이중 약속에 대한 처리 (약속 날짜와 오늘 날짜 비교)
            int comResult = compareDay(plans.getPlanDate(), today);
            if (comResult == 0) {
                // 3. 약속 시간 기준 +-2에 해당하는 약속은 정할 수 없게 처리
                // ex) 6시에 일정이 있으면 > 4시부터 8시 사이에는 일정을 잡지 못함
                long remainHours = getHours(planReqDto, plans);
                if (!(remainHours > 2 || remainHours < -2))
                    throw new IllegalArgumentException("오늘 일정은 이미 있습니다.");
            }
        }
        String url = UUID.randomUUID().toString();
        Plan plan = new Plan(planReqDto, user, url);
        planRepository.save(plan);
        plan.addPlan(user1);
        Participant participant = new Participant(plan, user1);
        participantRepository.save(participant);
        //eventPublisher.publishEvent(new PlanCreateEvent(participant));
    }



    // 일정 목록 조회  (05 -10 문제 상황 약속 시간 기준으로 일정을 정렬해서 보내주지 못하는 상황)
    public Page<PlanResDto> getPlanList(Long user_id, int pageno, User user) {

        List<Participant> participantList = participantRepository.findAllByUser(
                userRepository.findById(user_id).orElseThrow(IllegalArgumentException::new));

        List<Plan> plans = new ArrayList<>();

        for(Participant participant : participantList){
            Plan plan = participant.getPlan();
            plans.add(plan);
        }

        Pageable pageable = getPageable(pageno);
        List<PlanResDto> planResDtoList = new ArrayList<>();
        // 일정 시간 비교 메서드
        forPlanList(plans, planResDtoList, user);
        int start = pageno * 5;
        int end = Math.min((start + 5), plans.size());
        Page<PlanResDto> page = new PageImpl<>(planResDtoList.subList(start, end), pageable, planResDtoList.size());
        return page;

    }
    // 일정 리스트 만드는 메서드 > status를 통해 과거,현재,미래에 대한 일정 구분
    private void forPlanList(List<Plan> plans, List<PlanResDto> planResDtoList, User user) {
        for (Plan plan : plans) {
            int status = 0;
            LocalDateTime planDate = plan.getPlanDate();
            status = getStatus(status, planDate);
            Long planId = plan.getId();
            String planName = plan.getPlanName();
            Location locationDetail = plan.getLocation();
            String penalty = plan.getPenalty();
            // 작성자 판별
            boolean result = Objects.equals(plan.getWriter(), user.getNickname());
            String url = plan.getUrl();
            PlanResDto planResDto = new PlanResDto(planId, planName, planDate, locationDetail, status, result, url,penalty);
            planResDtoList.add(planResDto);
        }
    }

    // 일정 상세 조회
    public PlanDetailResDto getPlan(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(IllegalArgumentException::new);
        return new PlanDetailResDto(plan);
    }

    //일정 수정.
    //.작성자만 수정가능 , 약속 날짜는 과거 x ,
    @Transactional
    //@CachePut(value = CacheKey.PLAN, key ="#userDetails.user.id")
    public void editPlan(Long planid, PlanReqDto planRequestDto, User user) {
        Plan plan = planRepository.findById(planid).orElseThrow(IllegalArgumentException::new);
        LocalDateTime editTime = planRequestDto.getPlanDate();

        if (!Objects.equals(plan.getWriter(), user.getNickname())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        // 서울 현재시간 기준 , 예전이면 오류 발생 , 동일하게도 수정 불가 .
        if (LocalDateTime.now(ZoneId.of("Asia/Seoul")).isAfter(editTime)) {
            throw new IllegalArgumentException("만남 일정을 이미지난 날짜로 수정하는 것은 불가능합니다.");
        }
        plan.update(planRequestDto, editTime);
    }

    // 일정 삭제
    @Transactional
    public void deletePlan(Long planId, User user) {
        Plan plan = planRepository.findById(planId).orElseThrow(IllegalArgumentException::new);
        // 작성자만 삭제 가능
        if (Objects.equals(plan.getWriter(), user.getNickname())) {
            participantRepository.deleteByUserAndPlan(user,plan);
            planRepository.deleteById(planId);
        } else {
            throw new IllegalArgumentException("작성자만 삭제 가능합니다.");
        }
    }
}
