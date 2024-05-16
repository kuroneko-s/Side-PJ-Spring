package com.choidh.service.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncConfig implements AsyncConfigurer {
    private final AsyncExceptionIOHandler asyncExceptionIOHandler;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors); // 스레드 동작시 풀 사이즈.
        executor.setMaxPoolSize(processors * 2); // 스레드 동작시 최대 풀 사이즈
        executor.setQueueCapacity(50); // java의 Executor에서 사용되는 Queue 크기.
        executor.setKeepAliveSeconds(60); // 생명주기
        executor.setThreadNamePrefix("Input Executor - "); // 로그에 찍히는 스레드 이름 prefix
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncExceptionIOHandler;
    }
}
