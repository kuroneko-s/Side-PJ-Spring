package com.choidh.service.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * {@link AsyncUncaughtExceptionHandler} 비동기 동작에서 에러가 발생시 핸들링
 */

@Slf4j
@Component
public class AsyncExceptionIOHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        log.error("Async Uncaught Exception : {}", throwable.getMessage());
        log.error("error class name : {}", method.getDeclaringClass().getName() + ", {}" + method.getName());
    }
}
