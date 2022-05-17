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
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    // 메세지 전송을 위해 요청하는 주소
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/onit-a1529/messages:send";
//    private final String API_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String apiKey = "AAAAq97pb68:APA91bGZQJDZ58egw_deeq1qvG3eDiblvlgLQ_pOXLycCkUuqfIBXIE9dzvVix4Tec3svNDWuuJzpHSGUXXqeqJ_gs_WP98MNwXeFgRn95C7B3gFyWTZmgt4I4SzM8ef-swGy3rMjBZh";
    public static final String senderId = "738179248047";
    private final ObjectMapper objectMapper;
    private final PlanRepository planRepository;

    // 메세지 전송
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        log.info(targetToken, title, body);

        String message = makeMessage(targetToken, title, body);
        log.info(message);

        OkHttpClient client = new OkHttpClient();
        okhttp3.RequestBody requestBody = new FormBody.Builder()
                .add("to", targetToken)
                .add("project_id",senderId)
                .add("notification","")
                .add("data","제발 성공하자!")
                .build();
//        RequestBody requestBody = RequestBody.create(message,
//                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
//                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "key=" + apiKey)
//                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer" + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();
        log.info("헤더 토큰 확인"+getAccessToken());
        Response response = client.newCall(request).execute();

        log.info(response.toString());

        System.out.println(response.body().string());
    }

    @Transactional
    // 스케줄러 실행 - 10분 마다 // 자정
    @Scheduled(cron = "0 0/3 * * * *")
    public void noticeScheduler() throws InterruptedException {
        log.info(new Date() + "스케쥴러 실행");
        // 오늘의 날짜 구하기
        LocalDate today = LocalDate.now(); // 2022-05-14
        log.info(today.toString());
        LocalDateTime todayTime = today.atStartOfDay(); // 2022-05-14 00:00
        log.info(todayTime.toString());
        LocalDateTime tommorrowTime = todayTime.plusDays(1); // 2022-05-15 00:00
        log.info(tommorrowTime.toString());

        ExecutorService executorService = Executors.newCachedThreadPool();

        // 현 시각 기준으로 오늘의 plan List를 조회 - isAllowed true & 일정이 오늘인 약속들만
        List<Plan> planList = planRepository.findAllByPlanDateBetween(todayTime, tommorrowTime);
        log.info("DB 조회 완료");
        System.out.println(planList.size());


        // 조회한 plan List 반복문 실행
        for (Plan plan : planList) {
            executorService.execute(task(plan.getUser().getToken()));
            log.info(plan.getUser().getToken());
            // 현재시간 기준 1시간 후 = alarmHours
//            LocalDateTime alarmHour = LocalDateTime.now().plusHours(1);
//            log.info(String.valueOf(alarmHour));
//            // 한시간 뒤 시간으로 PlanDate가 있다면
//            if (alarmHour == plan.getPlanDate()) {
//                // 조건에 맞는 plan의 user.token과 1을 알림 전송 메소드(task)로 보내기
//                executorService.execute(task(plan.getUser().getToken(), 1L));
//            }
        }
    }

    public Runnable task(String token) {
        log.info(token);
        return () -> {
            try {
//                String body = String.format("약속 시간 1시간 전입니다!\n%s",l);
                String body = "약속 시간 1시간 전입니다!";
                sendMessageTo(token, "온잇(Onit)", body);
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

//        private static String getAccessToken() throws IOException {
//            GoogleCredentials googleCredentials = GoogleCredentials
//                    .fromStream(new FileInputStream("serviceAccount.json"))
//                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
//            googleCredentials.refreshAccessToken();
//            log.info("FCM access token 발급 성공");
//            log.info(googleCredentials.getAccessToken().getTokenValue());
//            return googleCredentials.getAccessToken().getTokenValue();
//        }

    private String getAccessToken() throws IOException {
//        String firebaseConfigPath = "/Users/zisoon/Desktop/hanghae99/Onit_BE/src/main/resources/firebase/serviceAccount.json";
//        log.info(firebaseConfigPath);

        GoogleCredentials googleCredential = GoogleCredentials.fromStream(new ClassPathResource("/firebase/onit-a1529-firebase-adminsdk-dw4dd-9a45434228.json")
                .getInputStream()).createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));

        googleCredential.refreshIfExpired();
//        googleCredential.refreshAccessToken();
        log.info("FCM access token 발급 성공");
        log.info("헤더 토큰"+googleCredential.getAccessToken().getTokenValue());
        return googleCredential.getAccessToken().getTokenValue();
    }


//    private String getAccessToken() throws IOException {
//// Load the service account key JSON file
////        FileInputStream serviceAccount = new FileInputStream("onit-a1529-firebase-adminsdk-dw4dd-b029d9c07e");
//
//// Authenticate a Google credential with the service account
////        GoogleCredentials googleCred = GoogleCredentials.fromStream(serviceAccount);
//        GoogleCredentials googleCred = GoogleCredentials.fromStream(new ClassPathResource("/firebase/onit-a1529-firebase-adminsdk-dw4dd-b029d9c07e.json")
//                .getInputStream());
//
//// Add the required scopes to the Google credential
//        GoogleCredentials scoped = googleCred.createScoped(
//                Arrays.asList(
//                        "https://www.googleapis.com/auth/firebase.database",
//                        "https://www.googleapis.com/auth/userinfo.email"
//                )
//        );
//
//// Use the Google credential to generate an access token
//        scoped.refreshIfExpired();
//        String token = scoped.getAccessToken().getTokenValue();
//        return token;
//
//// See the "Using the access token" section below for information
//// on how to use the access token to send authenticated requests to the
//// Realtime Database REST API.
//    }
}