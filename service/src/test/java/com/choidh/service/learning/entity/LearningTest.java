package com.choidh.service.learning.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.learning.repository.LearningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@ActiveProfiles("dev")
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LearningTest {
    private final AccountRepository accountRepository;
    private final LearningRepository learningRepository;

    @RepeatedTest(10)
    public void createLearning() throws Exception {
        Account account = accountRepository.getOne(98L);

        Learning learning = Learning.builder()
                .title("test learning")
                .simpleSubscription("simple subscription")
                .subscription("subscription")
                .price(10000)
                .rating(5)
                .tags(Collections.emptySet())
                .questions(Collections.emptySet())
                .reviews(Collections.emptySet())
                .build();
        learningRepository.save(learning);
    }
}