package com.hanghae99.onit_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableJpaAuditing
@SpringBootApplication
@EnableCaching
@EnableRedisHttpSession
public class OnitBeApplication{

//    public static void main(String[] args) {
//        SpringApplication.run(OnitBeApplication.class, args);
//    }

//    // S3
    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.properties,"
            + "classpath:aws.yml";
//    // S3
//    // application.yml과 aws.yml 두개의 파일 모두를 설정 파일로 읽어서 사용
    public static void main(String[] args) {
        new SpringApplicationBuilder(OnitBeApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }
}