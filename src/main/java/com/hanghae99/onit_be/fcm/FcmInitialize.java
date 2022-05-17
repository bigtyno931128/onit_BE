package com.hanghae99.onit_be.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

// 어플리케이션이 시작될 때 Firebase 프로젝트에 앱을 등록해줘야 한다.
// 두번 등록 되면 에러가 나므로 시작할 때 초기화 필요
@Service
public class FcmInitialize {

    private static final Logger logger = LoggerFactory.getLogger(FcmInitialize.class);
//    private static final String FIREBASE_CONFIG_PATH = "firebase/serviceAccount.json";
    private static final String FIREBASE_CONFIG_PATH = "src/main/resources/firebase/onit-a1529-firebase-adminsdk-dw4dd-9a45434228.json";

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream()))
//                    .setDatabaseUrl("https://onit-a1529-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
//                logger.info("Firebase application has been initialized");
                logger.info("FCM 초기화 성공");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

//    @PostConstruct
//    public void initialize() throws FileNotFoundException {
//        FileInputStream refreshToken = new FileInputStream("firebase/serviceAccount.json");
////        FileInputStream refreshToken = new FileInputStream("path/to/refreshToken.json");
//        try {
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.getApplicationDefault())
//                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
//                    .setDatabaseUrl("https://onit-a1529-default-rtdb.asia-southeast1.firebasedatabase.app")
//                    .build();
//            FirebaseApp.initializeApp(options);
////                logger.info("Firebase application has been initialized");
//            logger.info("FCM 초기화 성공");
//            logger.info(String.valueOf(refreshToken));
//        } catch (FileNotFoundException e) {
//            logger.error("Firebase ServiceAccountKey FileNotFoundException" + e.getMessage());
//        } catch (IOException e) {
//            logger.error("FirebaseOptions IOException" + e.getMessage());
//        }
//    }

}