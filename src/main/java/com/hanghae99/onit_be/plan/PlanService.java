package com.hanghae99.onit_be.plan;

import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.mypage.ParticipantRepository;
import com.hanghae99.onit_be.noti.event.LeaveEvent;
import com.hanghae99.onit_be.noti.event.PlanDeleteEvent;
import com.hanghae99.onit_be.noti.event.PlanUpdateEvent;
import com.hanghae99.onit_be.plan.dto.*;
import com.hanghae99.onit_be.user.UserRepository;
import com.hanghae99.onit_be.entity.Weather;
import com.hanghae99.onit_be.weather.WeatherCreateEvent;
import com.hanghae99.onit_be.weather.WeatherRepository;
import com.hanghae99.onit_be.weather.WeatherUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.hanghae99.onit_be.common.utils.Date.*;
import static com.hanghae99.onit_be.common.utils.Page.getPageable;
import static com.hanghae99.onit_be.common.utils.Valid.distance;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final WeatherRepository weatherRepository;


    // 일정 생성
    @Transactional
    public void createPlan(PlanReqDto planReqDto, User user) {

        // 지난 날짜로 등록 x
        checkPlanDate(planReqDto);

        // 약속 제목 (20자 이상 x )
        if (planReqDto.getPlanName().length() > 10 ) {
            throw new IllegalArgumentException("약속 제목은 10 자 이상 등록 할 수 없습니당~ >,,< 😂");
        }

        List<Participant> participantList = participantRepository.findAllByUserOrderByPlanDate(user);
        List<Plan> planList = new ArrayList<>();
        for (Participant participant : participantList) {
            Plan plan = participant.getPlan();
            planList.add(plan);
        }

        LocalDateTime today = planReqDto.getPlanDate();

        for (Plan plans : planList) {

            int comResult = compareDay(plans.getPlanDate(), today);
            long remainHours = getHours(planReqDto, plans);
            //이중 약속에 대한 처리 (약속 날짜와 오늘 날짜 비교, 약속 시간도 비교)
            checkPlan(comResult, remainHours);

        }
        String url = UUID.randomUUID().toString();
        Plan plan = new Plan(planReqDto, user, url);

        planRepository.save(plan);

        Participant participant = new Participant(plan, user);
        participantRepository.save(participant);

        eventPublisher.publishEvent(new WeatherCreateEvent(plan));
    }

    // 일정 상세 조회
    public PlanDetailResDto getPlan(String url, User user) {
        // 참가자  , 작성자의 planDetail
        Plan plan = planRepository.findByUrl(url);
        // 참여자들 조회 . plan 에 참여한
        List<Participant> participantList = participantRepository.findAllByPlan(plan);
        List<ParticipantDto> participantDtoList = new ArrayList<>();

        boolean isMember = false;

        for (Participant participant : participantList) {

            if (Objects.equals(participant.getUser().getId(), user.getId())) {
                isMember = true;
            }

            ParticipantDto participantDto = new ParticipantDto(participant);
            participantDtoList.add(participantDto);
        }

        return new PlanDetailResDto(plan, isMember, participantDtoList);
    }


    //일정 수정.
    //.작성자만 수정가능 , 약속 날짜는 과거 x ,
    @Transactional
    //@CachePut(value = CacheKey.PLAN, key ="#userDetails.user.id")
    public void editPlan(String url, PlanReqDto planRequestDto, User user) {

        Plan plan = planRepository.findByUrl(url);
        eventPublisher.publishEvent(new PlanUpdateEvent(plan, "일정을 수정 했습니다.", user));
        if (!Objects.equals(plan.getWriter(), user.getNickname())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        // 서울 현재시간 기준 , 예전이면 오류 발생 , 동일하게도 수정 불가 .
        checkPlanDate(planRequestDto);
        plan.update(planRequestDto, user);
        // send (참가자들 한테 만 )
        eventPublisher.publishEvent(new WeatherUpdateEvent(plan));
    }

    // 일정 삭제
    @Transactional
    public void deletePlan(String url, User user) {
        Plan plan = planRepository.findByUrl(url);
        
        // 작성자만 삭제 가능
        if (Objects.equals(plan.getWriter(), user.getNickname())) {
            eventPublisher.publishEvent(new PlanDeleteEvent(plan, "일정을 삭제 했습니다.", user));
            participantRepository.deleteByUserAndPlan(user, plan);
            weatherRepository.deleteAllByPlanId(plan.getId());
            planRepository.deleteByUrl(url);


        } else {
            eventPublisher.publishEvent(new LeaveEvent(plan, "일정을 취소 했습니다.", user));
            participantRepository.deleteByUserAndPlan(user, plan);

        }
    }


    // 일정 목록 조회 / 과거를 제외 하고
    // 400 예외 처리 필요
    public TwoPlanResDto getPlansList(User user, int pageno) {
        // 사용자 정보로 참여하는 일정 리스트 불러오기
        List<Participant> participantList = participantRepository.findAllByUserOrderByPlanDate(userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new));
        // 내가 만든 일정 리스트 초기화
        List<PlanResDto.MyPlanDto> myPlanList = new ArrayList<>();
        // 공유 받은 일정 리스트 초기화
        List<PlanResDto.MyPlanDto> invitedPlanList = new ArrayList<>();

        List<PlanResDto.MyPlanDto> totalPlanList = new ArrayList<>();

        for (Participant participant : participantList) {

            if (LocalDateTime.now(ZoneId.of("Asia/Seoul")).isBefore(participant.getPlan().getPlanDate())) {

                Long planId = participant.getPlan().getId();
                String planName = participant.getPlan().getPlanName();
                LocalDateTime planDate = participant.getPlan().getPlanDate();
                String locationName = participant.getPlan().getLocation().getName();
                String url = participant.getPlan().getUrl();
                String penalty = participant.getPlan().getPenalty();
                
                String description = "없음";

                LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

                LocalDate weatherDate = LocalDate.from(planDate.truncatedTo(ChronoUnit.DAYS));


                // plan Date 가 오늘 날짜 기준 + 8 이라면
                if (weatherDate.isBefore(today.plusDays(7))) {

                    Weather weather = weatherRepository.findByWeatherDateAndPlanId(weatherDate, planId);
                    description = weather.getDescription();

                }

                PlanResDto.MyPlanDto myPlanDto = new PlanResDto.MyPlanDto(planId, planName, planDate, locationName, url, description, penalty);
                totalPlanList.add(myPlanDto);

                // 작성자가 사용자이면 myPlanListDto에 담아주기
                if (Objects.equals(participant.getPlan().getWriter(), user.getNickname())) {
                    myPlanList.add(myPlanDto);
                } else {
                    invitedPlanList.add(myPlanDto);
                }
            }
        }

        Pageable pageable = getPageable(pageno);

        int start = pageno * 5;
        // myPlanList
        int end = Math.min((start + 5), myPlanList.size());
        // invitedPlanList
        int end2 = Math.min((start + 5), invitedPlanList.size());

        int end3 = Math.min((start + 5), totalPlanList.size());

        Page<PlanResDto.MyPlanDto> myPlanPage = new PageImpl<>(myPlanList.subList(start, end), pageable, myPlanList.size());

        Page<PlanResDto.MyPlanDto> invitedPlanPage = new PageImpl<>(invitedPlanList.subList(start, end2), pageable, invitedPlanList.size());

        Page<PlanResDto.MyPlanDto> totalPlanPage = new PageImpl<>(totalPlanList.subList(start, end3), pageable, totalPlanList.size());

        PlanListResDto.PlanListsResDto myPlanListsResDto = new PlanListResDto.PlanListsResDto(myPlanPage);
        PlanListResDto.PlanListsResDto invitedPlanListsResDto = new PlanListResDto.PlanListsResDto(invitedPlanPage);
        PlanListResDto.PlanListsResDto totalPlanListsResDto = new PlanListResDto.PlanListsResDto(totalPlanPage);

        return new TwoPlanResDto(myPlanListsResDto, invitedPlanListsResDto, totalPlanListsResDto);
    }


    public PlanListResDto.PlanListsResDto getMyPlansList(User user, int pageno) {

        List<Plan> planList = planRepository.findAllByUserOrderByPlanDateDesc(user);
        // 내가 만든 일정 리스트 초기화
        List<PlanResDto.MyPlanDto> myPlanList = new ArrayList<>();
        for (Plan plan : planList) {
            if (LocalDateTime.now(ZoneId.of("Asia/Seoul")).isBefore(plan.getPlanDate())) {
                Long planId = plan.getId();
                String planName = plan.getPlanName();
                LocalDateTime planDate = plan.getPlanDate();
                String locationName = plan.getLocation().getName();
                String url = plan.getUrl();
                String penalty = plan.getPenalty();
                String description = "Onit 서비스에서는 8일치 날씨예보만 제공 합니다.";
                LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                LocalDate weatherDate = LocalDate.from(planDate.truncatedTo(ChronoUnit.DAYS));
                // plan Date 가 오늘 날짜 기준 + 8 이라면
                if (weatherDate.isBefore(today.plusDays(8))) {
                    Weather weather = weatherRepository.findByWeatherDateAndPlanId(weatherDate, planId);
                    description = weather.getDescription();
                }
                PlanResDto.MyPlanDto myPlanDto = new PlanResDto.MyPlanDto(planId, planName, planDate, locationName, url, description, penalty);
                myPlanList.add(myPlanDto);
            }
        }
        Pageable pageable = getPageable(pageno);

        int start = pageno * 5;
        // myPlanList
        int end = Math.min((start + 5), myPlanList.size());

        Page<PlanResDto.MyPlanDto> myPlanPage = new PageImpl<>(myPlanList.subList(start, end), pageable, myPlanList.size());
        return new PlanListResDto.PlanListsResDto(myPlanPage);
    }


    public  PlanListResDto.PlanListsResDto getTotalPlansList(User user, int pageno) {
        // 사용자 정보로 참여하는 일정 리스트 불러오기
        List<Participant> participantList = participantRepository.findAllByUserOrderByPlanDate(userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new));

        List<PlanResDto.MyPlanDto> totalPlanList = new ArrayList<>();

        for (Participant participant : participantList) {

            if (LocalDateTime.now(ZoneId.of("Asia/Seoul")).isBefore(participant.getPlan().getPlanDate())) {

                Long planId = participant.getPlan().getId();
                String planName = participant.getPlan().getPlanName();
                LocalDateTime planDate = participant.getPlan().getPlanDate();
                String locationName = participant.getPlan().getLocation().getName();
                String url = participant.getPlan().getUrl();
                String penalty = participant.getPlan().getPenalty();
                String description = "Onit 서비스에서는 8일치 날씨예보만 제공 합니다.";
                LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                LocalDate weatherDate = LocalDate.from(planDate.truncatedTo(ChronoUnit.DAYS));
                // plan Date 가 오늘 날짜 기준 + 8 이라면
                if (weatherDate.isBefore(today.plusDays(8))) {

                    Weather weather = weatherRepository.findByWeatherDateAndPlanId(weatherDate, planId);
                    description = weather.getDescription();

                }

                PlanResDto.MyPlanDto myPlanDto = new PlanResDto.MyPlanDto(planId, planName, planDate, locationName, url, description, penalty);
                totalPlanList.add(myPlanDto);
            }
        }

        Pageable pageable = getPageable(pageno);
        int start = pageno * 5;
        int end3 = Math.min((start + 5), totalPlanList.size());
        Page<PlanResDto.MyPlanDto> totalPlanPage = new PageImpl<>(totalPlanList.subList(start, end3), pageable, totalPlanList.size());
        return new PlanListResDto.PlanListsResDto(totalPlanPage);
    }
}
