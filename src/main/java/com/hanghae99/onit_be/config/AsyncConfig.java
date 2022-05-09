package com.hanghae99.onit_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        //현재 시스템의 프로세서 개수 가져오기 .
        int processors = Runtime.getRuntime().availableProcessors();
        // 코어 size 는 현재 프로세서 갯수 만큼 (cpu or 하는 작업에 따라 달라짐)
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        // 메모리에 따라 달라짐
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }
}
