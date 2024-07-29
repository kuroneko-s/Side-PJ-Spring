package com.choidh.scheduler.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExampleTasks {
    // 초기 딜레이 2초 후 시작, 이후 매 3초마다 실행
    @Scheduled(initialDelay = 2000, fixedDelay = 3000)
    public void fixedDelayTask() {
        log.info("Fixed delay task - {}", System.currentTimeMillis());
    }

    // 매일 오전 1시 실행
    @Scheduled(cron = "0 0 1 * * ?")
    public void cronTask() {
        log.info("cronTask task - {}", System.currentTimeMillis());
    }
}
