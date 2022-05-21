package com.hanghae99.onit_be.plan;

import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.mypage.ParticipantRepository;
import com.hanghae99.onit_be.noti.event.PlanDeleteEvent;
import com.hanghae99.onit_be.noti.event.PlanUpdateEvent;
import com.hanghae99.onit_be.plan.dto.*;
import com.hanghae99.onit_be.user.UserRepository;
import com.hanghae99.onit_be.weather.Weather;
import com.hanghae99.onit_be.weather.WeatherCreateEvent;
import com.hanghae99.onit_be.weather.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.format.DateTimeFormatter;

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

        User user1 = userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new);
        // 이중 약속 유효성 검사
        // 1. 로그인한 유저의 닉네임으로 저장된 모든 plan list 조회
        List<Participant> participantList = participantRepository.findAllByUserOrderByPlanDate(user1);
        List<Plan> planList = new ArrayList<>();
        for (Participant participant : participantList) {
            Plan plan = participant.getPlan();
            planList.add(plan);
        }

        LocalDateTime today = planReqDto.getPlanDate();
        for (Plan plans : planList) {
            // 2. 이중 약속에 대한 처리 (약속 날짜와 오늘 날짜 비교)
            int comResult = compareDay(plans.getPlanDate(), today);
            long remainHours = getHours(planReqDto, plans);
            checkPlan(comResult,remainHours);

        }
        String url = UUID.randomUUID().toString();
        Plan plan = new Plan(planReqDto, user, url);
        planRepository.save(plan);
        plan.addPlan(user1);
        Participant participant = new Participant(plan, user1);
        participantRepository.save(participant);
        eventPublisher.publishEvent(new WeatherCreateEvent(plan));
    }


    // 일정 목록 조회 (내가 만든 일정 목록과 초대받은 일정 목록)
    // 400 예외 처리 필요
    public TwoPlanResDto getPlansList(User user, int pageno) {
        // 사용자 정보로 참여하는 일정 리스트 불러오기
        List<Participant> participantList = participantRepository.findAllByUserOrderByPlanDate(userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new));
        // 내가 만든 일정 리스트 초기화
        List<PlanResDto.MyPlanDto> myPlanList = new ArrayList<>();
        // 공유 받은 일정 리스트 초기화
        List<PlanResDto.MyPlanDto> invitedPlanList = new ArrayList<>();

        for (Participant participant : participantList) {
            Long planId = participant.getPlan().getId();
            String planName = participant.getPlan().getPlanName();
            String planDateCv = participant.getPlanDate().format(DateTimeFormatter.ofPattern("M월 d일 E요일 HH:mm").withLocale(Locale.forLanguageTag("ko")));
            String address = participant.getPlan().getLocation().getAddress();
            String url = participant.getPlan().getUrl();
            int status = 0;
            status = getStatus(status, participant.getPlanDate());

            List<Weather> weatherList = weatherRepository.findAllByPlanId(planId);
            String description = "일정 약속 당일에만 날씨정보를 제공 합니다.";

            // 개선방향 --> 1. for문을 돌리지 않으려면 , 날씨 테이블에서 처음부터 오늘 날짜에 해당하는 레코드만 불러올 수 있도록 생각 해야 할듯 ??
            //  2. (현재 ) 참여하려는 일정 중에 오늘일정이 있으면 날씨 정보를 제공 하고 있는데 , 만약 프론트에서 홈 화면에 보여줄 때  오늘 일정이 없으면 ? 어떻게 보여주는지
            //  3. 알아야 할 것 같다 .

//            for (Weather weather : weatherList) {
//                // 일정 날짜가 오늘 일 때 만 날씨 정보를 제공 ?
//                log.info("일정 날짜=={}", participant.getPlan().getPlanDate().truncatedTo(ChronoUnit.DAYS));
//                log.info("날씨데이터 날짜=={}", weather.getWeatherDate().truncatedTo(ChronoUnit.DAYS));
//                int comResult = compareDay(weather.getWeatherDate(), participant.getPlanDate());
//                int comResult1 = compareDay(participant.getPlan().getPlanDate(), LocalDateTime.now(ZoneId.of("Asia/Seoul")));
//                if (comResult1 == 0 && comResult == 0) {
//                    description = weather.getDescription();
//                }
//            }

            PlanResDto.MyPlanDto myPlanDto = new PlanResDto.MyPlanDto(planId, planName, planDateCv, address, url, status, description);

            // 작성자가 사용자이면 myPlanListDto에 담아주기
            if (Objects.equals(participant.getWriter(), user.getNickname())) {
                log.info("????");
                myPlanList.add(myPlanDto);
            } else {
                invitedPlanList.add(myPlanDto);
            }
        }

        Pageable pageable = getPageable(pageno);

        int start = pageno * 5;
        // myPlanList
        int end = Math.min((start + 5), myPlanList.size());
        // invitedPlanList
        int end2 = Math.min((start + 5), invitedPlanList.size());

        Page<PlanResDto.MyPlanDto> myPlanPage = new PageImpl<>(myPlanList.subList(start, end), pageable, myPlanList.size());
        Page<PlanResDto.MyPlanDto> invitedPlanPage = new PageImpl<>(invitedPlanList.subList(start, end2), pageable, invitedPlanList.size());

        PlanListResDto.PlanListsResDto myPlanListsResDto = new PlanListResDto.PlanListsResDto(myPlanPage);
        PlanListResDto.PlanListsResDto invitedPlanListsResDto = new PlanListResDto.PlanListsResDto(invitedPlanPage);

        return new TwoPlanResDto(myPlanListsResDto, invitedPlanListsResDto);
    }


    // 일정 리스트 만드는 메서드 > status를 통해 과거,현재,미래에 대한 일정 구분
    private void forPlanList(List<Plan> planList, List<PlanResDto> planResDtoList, User user) {
        for (Plan plan : planList) {
            int status = 0;
            LocalDateTime planDate = plan.getPlanDate();
            status = getStatus(status, planDate);
            Long planId = plan.getId();
            String planName = plan.getPlanName();
            Location locationDetail = plan.getLocation();
            String penalty = plan.getPenalty();
            String writer = plan.getWriter();
            // 작성자 판별
            boolean isMember = false;
            for (Participant participant : plan.getParticipantList()) {
                isMember = participant.isMember();
            }

            String url = plan.getUrl();
            PlanResDto planResDto = new PlanResDto(planId, planName, planDate, locationDetail, status, url, penalty, writer, isMember);
            planResDtoList.add(planResDto);
        }
    }

    // 일정 상세 조회
    public PlanDetailResDto getPlan(String url, User user) {
        Plan plan = planRepository.findByUrl(url);
        if (!Objects.equals(plan.getWriter(), user.getNickname())) {
            plan.updateJoin2();
            boolean isMember = plan.isMember();
            return new PlanDetailResDto(plan,isMember);
        } else {
            boolean isMember = plan.isMember();
            return new PlanDetailResDto(plan, isMember);
        }
    }

    //일정 수정.
    //.작성자만 수정가능 , 약속 날짜는 과거 x ,
    @Transactional
    //@CachePut(value = CacheKey.PLAN, key ="#userDetails.user.id")
    public void editPlan(String url, PlanReqDto planRequestDto, User user) {
        Plan plan = planRepository.findByUrl(url);

        if (!Objects.equals(plan.getWriter(), user.getNickname())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        // 서울 현재시간 기준 , 예전이면 오류 발생 , 동일하게도 수정 불가 .
        checkPlanDate(planRequestDto);
        plan.update(planRequestDto);
        eventPublisher.publishEvent(new PlanUpdateEvent(plan, "일정을 수정했습니다.", user));
    }

    // 일정 삭제
    @Transactional
    public void deletePlan(String url, User user) {
        Plan plan = planRepository.findByUrl(url);
        // 작성자만 삭제 가능
        if (Objects.equals(plan.getWriter(), user.getNickname())) {
            participantRepository.deleteByUserAndPlan(user, plan);
            weatherRepository.deleteAllByPlanId(plan.getId());
            planRepository.deleteByUrl(url);
            eventPublisher.publishEvent(new PlanDeleteEvent(plan, "일정을 삭제 했습니다.", user));
        } else {
            throw new IllegalArgumentException("작성자만 삭제 가능합니다.");
        }
    }


    // 일정 목록 조회 / 과거를 제외 하고
    // 400 예외 처리 필요
    public TwoPlanResDto getTest(User user, int pageno) {
        // 사용자 정보로 참여하는 일정 리스트 불러오기
        List<Participant> participantList = participantRepository.findAllByUserOrderByPlanDate(userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new));
        // 내가 만든 일정 리스트 초기화
        List<PlanResDto.MyPlanDto> myPlanList = new ArrayList<>();
        // 공유 받은 일정 리스트 초기화
        List<PlanResDto.MyPlanDto> invitedPlanList = new ArrayList<>();

        for (Participant participant : participantList) {

            if (LocalDateTime.now(ZoneId.of("Asia/Seoul")).isBefore(participant.getPlanDate())) {

                Long planId = participant.getPlan().getId();
                String planName = participant.getPlan().getPlanName();
                String planDateCv = participant.getPlanDate().format(DateTimeFormatter.ofPattern("M월 d일 E요일 HH:mm").withLocale(Locale.forLanguageTag("ko")));
                String address = participant.getPlan().getLocation().getAddress();
                String url = participant.getPlan().getUrl();

                int status = 0;
                status = getStatus(status, participant.getPlanDate());
                LocalDate weatherDate = LocalDate.from(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));

                Weather weather = weatherRepository.findByWeatherDateAndPlanId(weatherDate, planId);
                String description = "일정 약속 당일에만 날씨정보를 제공 합니다.";
                int comResult = compareDay(participant.getPlan().getPlanDate(), LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                if (comResult == 0) {
                    description = weather.getDescription();
                }

                PlanResDto.MyPlanDto myPlanDto = new PlanResDto.MyPlanDto(planId, planName, planDateCv, address, url, status, description);

                // 작성자가 사용자이면 myPlanListDto에 담아주기
                if (Objects.equals(participant.getWriter(), user.getNickname())) {
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

        Page<PlanResDto.MyPlanDto> myPlanPage = new PageImpl<>(myPlanList.subList(start, end), pageable, myPlanList.size());
        Page<PlanResDto.MyPlanDto> invitedPlanPage = new PageImpl<>(invitedPlanList.subList(start, end2), pageable, invitedPlanList.size());

        PlanListResDto.PlanListsResDto myPlanListsResDto = new PlanListResDto.PlanListsResDto(myPlanPage);
        PlanListResDto.PlanListsResDto invitedPlanListsResDto = new PlanListResDto.PlanListsResDto(invitedPlanPage);

        return new TwoPlanResDto(myPlanListsResDto, invitedPlanListsResDto);
    }

    // 거리사 1키로 안쪽 일 때 도착신호 .

    public void getDistance(TestDto testDto, Long planId) {

        Plan plan = planRepository.findById(planId).orElseThrow(IllegalArgumentException::new);

        double a = plan.getLocation().getLng();
        double b = plan.getLocation().getLat();
        double c = testDto.getX();
        double d = testDto.getY();

        double distance = distance(a, b, c, d, "kilometer");
        int point = (int) Math.ceil(distance);
        log.info("목적지 까지의 거리 =={}", distance);


        String distnace = "가는중";
        log.info("거리=={}",point);
        if (1 >= point) {
            distnace = "도착";
            log.info(distnace);
        }
    }

}
