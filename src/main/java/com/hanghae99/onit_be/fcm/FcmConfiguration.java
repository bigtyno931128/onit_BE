//package com.hanghae99.onit_be.fcm;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.messaging.FirebaseMessaging;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//
//@Component
//@Configuration
//public class FcmConfiguration {
//
//    private FirebaseApp firebaseApp;
//
//    @Value("${fcm.key.path}")
//    private String firebaseSdkPath;
//
//
//    private static final Logger logger = LoggerFactory.getLogger(FcmConfiguration.class);
////    private static final String FIREBASE_CONFIG_PATH = "firebase/serviceAccount.json";
////    private static final String FIREBASE_CONFIG_PATH = "firebase/onit-a1529-firebase-adminsdk-dw4dd-b029d9c07e.json";
////
////    @PostConstruct
////    public void initialize() {
////        try {
////            FirebaseOptions options = FirebaseOptions.builder()
////                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream()))
////                    .setDatabaseUrl("https://onit-a1529-default-rtdb.asia-southeast1.firebasedatabase.app").build();
////            if (FirebaseApp.getApps().isEmpty()) {
////                firebaseApp = FirebaseApp.initializeApp(options);
//////                logger.info("Firebase application has been initialized");
////                logger.info("FCM 초기화 성공");
////            }
////        } catch (IOException e) {
////            logger.error(e.getMessage());
////        }
////    }
//
//    @PostConstruct
//    public FirebaseApp initializeFCM() throws IOException {
//        Resource resource = new ClassPathResource(firebaseSdkPath);
//        FileInputStream fis = new FileInputStream(resource.getFile());
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(fis))
//                .build();
//        firebaseApp = FirebaseApp.initializeApp(options, "Hooky");
//        logger.info("FCM 초기화 성공");
//        return firebaseApp;
//    }
//
//    @Bean
//    public FirebaseAuth initFirebaseAuth() {
//        FirebaseAuth instance = FirebaseAuth.getInstance(firebaseApp);
//        return instance;
//    }
//
//    @Bean
//    public FirebaseMessaging initFirebaseMessaging() {
//        FirebaseMessaging instance = FirebaseMessaging.getInstance(firebaseApp);
//        return instance;
//    }
//}