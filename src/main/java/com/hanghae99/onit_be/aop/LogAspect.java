package com.hanghae99.onit_be.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Component
@Aspect
public class LogAspect {

    Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // @LogExecutionTime 애노테이션이 붙어있는 타겟 메소드를 실행
        Object proceed = joinPoint.proceed();

        stopWatch.stop();
        logger.info(stopWatch.prettyPrint());

        return proceed; // 결과 리턴
    }

    // 클라이언트에서 보내주는 값 로깅
    @Around("@annotation(Logging)")
    public Object logPrint(ProceedingJoinPoint proceedingJoinPoint) throws  Throwable {

        logger.info("-------------------------------------");

        long start = System.currentTimeMillis();

        Object result = proceedingJoinPoint.proceed();

        long end = System.currentTimeMillis();

        logger.info("Parameter   :" + Arrays.toString(Arrays.stream(proceedingJoinPoint.getArgs()).toArray()));
        logger.info("Running Time :" + (end-start));
        logger.info("-------------------------------------");
        return result;
    }
}

