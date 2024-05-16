package com.choidh.service.common.timmer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class TimerService {

    @Around("@annotation(Timer)")
    public Object timerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

//        log.info("#################################################");
//        log.info("실행 시간(ms): " + stopWatch.getTotalTimeMillis());
        log.info(stopWatch.prettyPrint());

        return result;
    }
}
