package com.hanghae99.onit_be.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonParseException;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hanghae99.onit_be.utils.Date.getHours;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    // 메세지 전송을 위해 요청하는 주소
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/onit-a1529/messages:send";
//    private final String API_URL = "https://fcm.googleapis.com/fcm/send";
    private final ObjectMapper objectMapper;
    private final PlanRepository planRepository;

    // 메세지 전송
    public void sendMessageTo(String targetToken, String title, String body, String url) throws IOException {
        log.info(targetToken, title, body);

        String message = makeMessage(targetToken, title, body);
        log.info(message);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer" + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        log.info(response.toString());

        System.out.println(response.body().string());
    }

    @Transactional
    // 스케줄러 실행 - 10분 마다 // 자정
    @Scheduled(cron = "0 0/10 * * * *")
    public void noticeScheduler() throws InterruptedException {
        log.info(new Date() + "스케쥴러 실행");
        // 오늘의 날짜 구하기
        LocalDate today = LocalDate.now();
        LocalDateTime todayTime = today.atStartOfDay();
        LocalDateTime tommorrowTime = todayTime.plusDays(1);
        ExecutorService executorService = Executors.newCachedThreadPool();

        // 현 시각 기준으로 오늘의 plan List를 조회 - isAllowed true & 일정이 오늘인 약속들만
        List<Plan> planList = planRepository.findAllByPlanDateBetween(todayTime, tommorrowTime);
        log.info("DB 조회 완료");
        System.out.println(planList.size());

        // 현재 시간 구하기
        LocalDateTime now = LocalDateTime.now();
        // 조회한 plan List 반복문 실행
        for (Plan plan : planList){
        // 각 plan의 planDate와 현재 시간의 차이가 1(시간)이라면
            long alarmHour = ChronoUnit.HOURS.between(now, plan.getPlanDate());
            if (alarmHour == 1) {
                // 조건에 맞는 plan의 user.token과 url, 1을 알림 전송 메소드(task)로 보내기
                executorService.execute(task(plan.getUser().getToken(), plan.getUrl(), 1L));
            }
        }
    }

    public Runnable task(String token, String url, Long l) {
        return () -> {
            try {
                String body = String.format("약속 시간 1시간 전입니다!\n%s",l, url);
                sendMessageTo(token, "온잇(Onit)", body, url);
                log.info("push message 전송 요쳥");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("push message 전송 실패");
            }
        };
    }


    // 메세지 생성
    private String makeMessage(String targetToken, String title, String body) throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    // 메세지 전송을 위한 접근 토큰 발급
//    private String getAccessToken() throws IOException {
////        String firebaseConfigPath = "firebase/firebase_service_key.json";
//        String firebaseConfigPath = "firebase/onit-a1529-firebase-adminsdk-dw4dd-f48876342a.json";
//
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
//
//        googleCredentials.refreshIfExpired();
//        return googleCredentials.getAccessToken().getTokenValue();
//    }

        private static String getAccessToken() throws IOException {
            GoogleCredentials googleCredentials = GoogleCredentials
//                    .fromStream(new FileInputStream("onit-a1529-firebase-adminsdk-dw4dd-f48876342a.json"))
                    .fromStream(new FileInputStream("serviceAccount.json"))
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
//                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
            googleCredentials.refreshAccessToken();
            log.info("FCM access token 발급 성공");
            return googleCredentials.getAccessToken().getTokenValue();
        }

    public FcmResDto manualPush(Long planId, Long userId) {
        Plan plan = planRepository.findById(planId).orElseThrow(
                () -> new IllegalArgumentException("해당 일정이 없습니다.")
        );
        if (userId.equals(plan.getUser().getId())) {
            return FcmResDto.of(plan);
        }
        log.info("Account 정보가 일치하지 않습니다");
        throw new IllegalArgumentException("유저 정보가 없습니다.");
    }
}