package com.hanghae99.onit_be.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.mypage.ParticipantRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    // 단일 디바이스 푸쉬
    public void sendMessageTo( String topic, String title, String body, String url) throws IOException {
        log.info("1.토큰, 제목, 본문 확인====== " + topic +" ///// "+ title +" ///// "+ body);
        String message = makeMessage(topic, title, body, url);
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

//    private void sendSubscribeTopic(User user)
//            throws FirebaseMessagingException {
//        List<String> registrationTokens =
//                Collections.singletonList(user.getToken());
//
//        TopicManagementResponse response = FirebaseMessaging.getInstance()
//                .subscribeToTopic(Collections.singletonList
//                        (user.getToken()), "alram");
//
//        System.out.println(response.getSuccessCount() +
//                " tokens were subscribed successfully");
//
//        FcmMessage.Message fcmMessage = FcmMessage.message.builder()
//                    .setNotification(FcmMessage.Notification.builder()
//                        .setTitle("[Apple News] IPhone SE 출시 임박!!!")
//                        .setBody("빠르게 사전 예약 하세요. 주문이 폭주하고 있습니다.")
//                        .build())
//                    .topic("Apple News")
//                    .build();
//
//        String response2 = FirebaseMessaging.getInstance().send(message);
//        log.debug("Successfully sent message: " + response2);
//    }


    @Transactional
    @Scheduled(cron = "0 0/2 * * * *")
    public void noticeScheduler() throws InterruptedException, FirebaseMessagingException {
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
        log.info("5.현재 시간===== " + today);
        LocalDateTime todayTime = today.atStartOfDay(); // 2022-05-14 00:00
        log.info("6.오늘 시작 시간===== " + todayTime);
        LocalDateTime tommorrowTime = todayTime.plusDays(1); // 2022-05-15 00:00
        log.info("7.내일 시작 시간===== " + tommorrowTime);

        ExecutorService executorService = Executors.newCachedThreadPool();

        // 현 시각 기준으로 오늘의 plan List를 조회 - isAllowed true & 일정이 오늘인 약속들만
        List<Plan> planList = planRepository.findAllByPlanDateBetween(todayTime, tommorrowTime);
        log.info("8.DB 조회 완료");
        log.info("9.오늘의 일정의 수===== " + planList.size());

//        List<String> registerationTokens = Arrays.asList();
        List<String> registrationTokens = new ArrayList<>();
//        log.info("리스트가 비었는지 확인===== " + registrationTokens);
        String topic = "alarm";
        Thread.sleep(5000);
        // 조회한 plan List 반복문 실행
        for (Plan plan : planList) {
            // 현재시간 기준 1시간 후 = alarmHours
            LocalDateTime planDate = plan.getPlanDate();
            LocalDateTime alarmHour = LocalDateTime.now().plusHours(1);
            int alarm = compareHour(alarmHour, planDate);
            log.info("10.약속시간 확인==== " + plan.getPlanDate());
            log.info(String.valueOf(alarmHour.truncatedTo(ChronoUnit.MINUTES)));

            // 알림 시간 == 약속 시간일 때 사용자들에게 알림 푸쉬
            if (alarm == 0) {
//                Long planId = plan.getId();
                List<Participant> participantList = participantRepository.findAllById(plan.getId());
                for (Participant participant : participantList) {
                    log.info("참가자====" + participant.getUser().getNickname());
                    String userTokens = participant.getUser().getToken();
                    log.info("참가자 내 유저 토큰 뽑아내기===== " + userTokens);
//                    postTopic(userTokens, topic);
                    registrationTokens.add(userTokens);
                }
//                log.info("11.유저 토큰 리스트===== " + userTokens);
                // 유저들의 토큰들 모아서 구독
//                postTopic(registerationTokens, topic);
//                TopicManagementResponse topicDevice = FirebaseMessaging.getInstance().subscribeToTopic(registerationTokens, topic);
//                FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, "alarm");
//                log.info("11-1.토큰들 구독시켜주기!!!");
                String url = "https://imonit.co.kr/details/" + plan.getUrl();
                log.info("12.링크==== " + url);
//                executorService.execute(task(response, url));
                executorService.execute(task(topic, url));
                FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, "alarm");
                log.info("11-1.토큰들 구독시켜주기!!!");
//                executorService.execute(task(registerationTokens, url));
            }
        }
    }

//     디바이스 토큰 > 주제 구독시키는 메서드
//    public void postTopic( String deviceToken, String topic ) throws FirebaseMessagingException {
//        List<String> registrationTokens = Arrays.asList(deviceToken);
//        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
////        FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
//    }


    public static int compareHour(LocalDateTime date1, LocalDateTime date2) {
        LocalDateTime dayDate1 = date1.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime dayDate2 = date2.truncatedTo(ChronoUnit.MINUTES);
        int compareResult = dayDate1.compareTo(dayDate2);
        return compareResult;
    }

    public Runnable task(String topic, String url) {
        return () -> {
            try {
                String body = "약속 시간 1시간 전입니다!";
                sendMessageTo(topic,"온잇(Onit)", body, url);
                log.info("13.push message 전송 요쳥");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("14.push message 전송 실패");
            }
        };
    }


    private String makeMessage( String topic, String title, String body, String url) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
//                        .registration_ids(targetTokens)
//                        .token(targetTokens)
                        .topic(topic)
//                        .allTolkens(registerationTokens)
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
        log.info("15.FCM access token 발급 성공");
        return googleCredential.getAccessToken().getTokenValue();
    }
}