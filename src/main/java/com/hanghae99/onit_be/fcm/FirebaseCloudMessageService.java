package com.hanghae99.onit_be.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.auth.oauth2.GoogleCredentials;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class FirebaseCloudMessageService {

    private final ObjectMapper objectMapper;
    private final PlanRepository planRepository;


    public void sendMessageTo(String targetToken, String title, String body, String url) throws IOException {
        String message = makeMessage(targetToken, title, body, url);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        String API_URL = "https://fcm.googleapis.com/v1/projects/onit-a1529/messages:send";
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        log.info(response.body().string());
    }

    @Transactional
    @Scheduled(cron = "0 0/5 * * * *")
    public void noticeScheduler() throws InterruptedException {
        log.info(new Date() + "스케쥴러 실행");
        //truncatedTo() 메소드는 파라미터로 지정된 단위 이후의 값들을 버린 후,복사한 LocalDateTime 객체를 리턴합니다.

        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        //List<Plan> planList = planRepository.findAllByNoticeTime(dateTime);
        log.info("DB 조회 완료");
        ExecutorService executorService = Executors.newCachedThreadPool();
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
        // DB 조회 완료할 때까지의 대기 시간
        Thread.sleep(5000);
//        for (Plan plan : planList) {
//            Long lastMinutes = ChronoUnit.MINUTES.between(plan.getNoticeTime(), plan.getPlanDate());
//            executorService.execute(task(plan.getUser().getToken(), plan.getUrl(), lastMinutes));
//        }
    }

    public Runnable task(String token, String url, Long lastMinutes) {
        return () -> {
            try {
                String body = String.format("모임시간 %d분 전입니다!\n%s", lastMinutes, url);
                sendMessageTo(token, "모두모여(Momo)", body, url);
                log.info("push message 전송 요쳥");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("push message 전송 실패");
            }
        };
    }

    private String makeMessage(String targetToken, String title, String body, String url) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .data(FcmMessage.FcmData.builder()
                                .url(url)
                                .build()
                        )
                        .build()
                )
                .validateOnly(false)
                .build();

        log.info(objectMapper.writeValueAsString(fcmMessage));
        return objectMapper.writeValueAsString(fcmMessage);
    }

    // AccessToken 발급 받기
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/onit-a1529-firebase-adminsdk-dw4dd-f48876342a.json";

        GoogleCredentials googleCredential = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath)
                .getInputStream()).createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

        googleCredential.refreshIfExpired();
        log.info("FCM access token 발급 성공");
        return googleCredential.getAccessToken().getTokenValue();
    }

    // 1)  plan 을 찾고 ,
    public FcmResponseDto manualPush(Long planId, Long userId) {
        Plan plan = planRepository.findById(planId).orElseThrow(
                IllegalArgumentException::new
        );
        if (userId.equals(plan.getUser().getId())) {
            return FcmResponseDto.of(plan);
        }
        log.info("Account 정보가 일치하지 않습니다");
        throw new IllegalArgumentException();
    }

}