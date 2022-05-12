package com.hanghae99.onit_be.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    // 메세지 전송을 위해 요청하는 주소
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/onit-a1529/messages:send";
//    private final String API_URL = "https://fcm.googleapis.com/fcm/send";
    private final ObjectMapper objectMapper;

    // 메세지 전송
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
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
                    .fromStream(new FileInputStream("firbase/serviceAccount.json"))
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
//                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
            googleCredentials.refreshAccessToken();
            log.info("FCM access token 발급 성공");
            return googleCredentials.getAccessToken().getTokenValue();
        }
}