//package com.hanghae99.onit_be.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.jasypt.encryption.StringEncryptor;
//import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
//import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
//
//@Configuration
//public class JasyptConfig {
//
//    @Value("${jasypt.encryptor.password}")
//    private String encryptKey;
//
////    @Value("${jasypt.encryptor.algorithm}")
////    private String algorithm;
////
////    @Value("${jasypt.encryptor.poolSize}")
////    private String poolSize;
//
//
//    @Bean("jasyptStringEncryptor")
//    public StringEncryptor stringEncryptor() {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        System.out.println("encryptKey : " + encryptKey);
//        config.setPassword(encryptKey);
////        config.setPassword(System.getenv("JASYPT_PASSWORD")); // 환경 변수
//        config.setAlgorithm("PBEWithMD5AndDES");
//        config.setKeyObtentionIterations("1000");
//        config.setPoolSize("1");
//        config.setProviderName("SunJCE");
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
//        config.setStringOutputType("base64");
//        encryptor.setConfig(config);
//        return encryptor;
//    }
//}
