//package com.hanghae99.onit_be.fcm;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.hanghae99.onit_be.entity.Plan;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Slf4j
//@Service
//public class FcmService {
//    public String sendMessageTo(String targetToken, String title, String body) throws FirebaseMessagingException {
//
//        FcmMessage fcmMessage = FcmMessage.builder()
//                .message(FcmMessage.Message.builder()
//                        .token(targetToken)
//                        .notification(FcmMessage.Notification.builder()
//                                .title(title)
//                                .body(body)
//                                .image(null)
//                                .build()
//                        ).build()).validateOnly(false).build();
//
//        // Send a message to the device corresponding to the provided
//        // registration token.
//        String response = FirebaseMessaging.getInstance().send(fcmMessage);
//        // Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);
//
//        return response;
//    }
//
//    @Transactional
//    // 스케줄러 실행 - 10분 마다 // 자정
//    @Scheduled(cron = "0 0/3 * * * *")
//    public void noticeScheduler() throws InterruptedException {
//        log.info(new Date() + "스케쥴러 실행");
//        // 오늘의 날짜 구하기
//        LocalDate today = LocalDate.now(); // 2022-05-14
//        log.info(today.toString());
//        LocalDateTime todayTime = today.atStartOfDay(); // 2022-05-14 00:00
//        log.info(todayTime.toString());
//        LocalDateTime tommorrowTime = todayTime.plusDays(1); // 2022-05-15 00:00
//        log.info(tommorrowTime.toString());
//
//        ExecutorService executorService = Executors.newCachedThreadPool();
//
//        // 현 시각 기준으로 오늘의 plan List를 조회 - isAllowed true & 일정이 오늘인 약속들만
//        List<Plan> planList = planRepository.findAllByPlanDateBetween(todayTime, tommorrowTime);
//        log.info("DB 조회 완료");
//        System.out.println(planList.size());
//
//
//        // 조회한 plan List 반복문 실행
//        for (Plan plan : planList){
//            executorService.execute(task(plan.getUser().getToken()));
//            log.info(plan.getUser().getToken());
//            // 현재시간 기준 1시간 후 = alarmHours
////            LocalDateTime alarmHour = LocalDateTime.now().plusHours(1);
////            log.info(String.valueOf(alarmHour));
////            // 한시간 뒤 시간으로 PlanDate가 있다면
////            if (alarmHour == plan.getPlanDate()) {
////                // 조건에 맞는 plan의 user.token과 1을 알림 전송 메소드(task)로 보내기
////                executorService.execute(task(plan.getUser().getToken(), 1L));
////            }
//        }
//    }
//
//    public Runnable task(String token) {
//        log.info(token);
//        return () -> {
//            try {
////                String body = String.format("약속 시간 1시간 전입니다!\n%s",l);
//                String body = "약속 시간 1시간 전입니다!";
//                sendMessageTo(token, "온잇(Onit)", body);
//                log.info("push message 전송 요쳥");
//            } catch (IOException e) {
//                e.printStackTrace();
//                log.error("push message 전송 실패");
//            }
//        };
//    }
//}
