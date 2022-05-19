package com.hanghae99.onit_be.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.plan.PlanRepository;
import com.hanghae99.onit_be.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class FirebaseCloudMessageService {

    private static final String API_URL = "https://fcm.googleapis.com/v1/projects/onit-a1529/messages:send";
    private final ObjectMapper objectMapper;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public void sendMessageTo(String targetToken, String title, String body, String url) throws IOException {
        log.info("1.토큰, 제목, 본문 확인====== " + targetToken +" ///// "+ title +" ///// "+ body);
        String message = makeMessage(targetToken, title, body, url);
        log.info("2.메세지 값 확인======= " + message);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + getAccessToken())
                .addHeader("Content-Type", "application/json; UTF-8")
                .build();
        log.info("3.헤더 토큰 확인======= " + getAccessToken());
        Response response = client.newCall(request).execute();

        log.info(Objects.requireNonNull(response.body()).string());
    }


    @Transactional
    @Scheduled(cron = "0 0/2 * * * *")
    public void noticeScheduler() {
        log.info(new Date() + "4.스케쥴러 실행");
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        String username = "soon";
//        User user = userRepository.findByUsername(username).orElseThrow (
//                () -> new IllegalArgumentException("")
//        );
//        log.info("DB 조회 완료===== " + user.getUsername());
//        executorService.execute(task(user.getToken()));
//        log.info("유저의 토큰 확인==== " + user.getToken());
        // 오늘의 날짜 구하기
        LocalDate today = LocalDate.now(); // 2022-05-14
        log.info("현재 시간===== " + today);
        LocalDateTime todayTime = today.atStartOfDay(); // 2022-05-14 00:00
        log.info("오늘 시작 시간===== " + todayTime);
        LocalDateTime tommorrowTime = todayTime.plusDays(1); // 2022-05-15 00:00
        log.info("내일 시작 시간===== " + tommorrowTime);

        ExecutorService executorService = Executors.newCachedThreadPool();

        // 현 시각 기준으로 오늘의 plan List를 조회 - isAllowed true & 일정이 오늘인 약속들만
        List<Plan> planList = planRepository.findAllByPlanDateBetween(todayTime, tommorrowTime);
        log.info("5.DB 조회 완료");
        log.info("6.오늘의 일정의 수===== " + planList.size());

        // 조회한 plan List 반복문 실행
        for (Plan plan : planList) {
            // 현재시간 기준 1시간 후 = alarmHours
            LocalDateTime planDate = plan.getPlanDate();
            LocalDateTime alarmHour = LocalDateTime.now().plusHours(1);
            int alarm = compareHour(alarmHour, planDate);
            log.info("8.약속시간 확인==== " + plan.getPlanDate());
            log.info(String.valueOf(alarmHour.truncatedTo(ChronoUnit.MINUTES)));

//            // 한시간 뒤 시간으로 PlanDate가 있다면
            if (alarm == 0) {
//                // 조건에 맞는 plan의 user.token과 1을 알림 전송 메소드(task)로 보내기
                String url = "https://imonit.co.kr/details/" + plan.getUrl();
                log.info("링크==== " + url);
                executorService.execute(task(plan.getUser().getToken(), url));
            }
        }
    }

    public static int compareHour(LocalDateTime date1, LocalDateTime date2) {
        LocalDateTime dayDate1 = date1.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime dayDate2 = date2.truncatedTo(ChronoUnit.MINUTES);
        int compareResult = dayDate1.compareTo(dayDate2);
        return compareResult;
    }


    public Runnable task(String token, String url) {
        return () -> {
            try {
                String body = "약속 시간 1시간 전입니다!";
                sendMessageTo(token, "온잇(Onit)", body, url);
                log.info("8.push message 전송 요쳥");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("9.push message 전송 실패");
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
        String firebaseConfigPath = "firebase/onit-a1529-firebase-adminsdk-dw4dd-94859bec82.json";

        GoogleCredentials googleCredential = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath)
                .getInputStream()).createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));

        googleCredential.refreshIfExpired();
        log.info("10.FCM access token 발급 성공");
        return googleCredential.getAccessToken().getTokenValue();
    }
}