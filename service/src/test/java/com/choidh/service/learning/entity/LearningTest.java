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
                .lecturerName("test 강사")
                .lecturerDescription("tessst 강사로 일했음")
                .simpleSubscription("simple subscription")
                .subscription("subscription")
                .bannerBytes(null)
                .bannerServerPath("")
                .kategorie("java")
                .price(10000)
                .createLearning(LocalDateTime.now())
                .openLearning(LocalDateTime.now())
                .closeLearning(null)
                .uploadVideo(null)
                .updateLearning(null)
                .buyLearning(LocalDateTime.now())
                .startingLearning(true)
                .closedLearning(false)
                .videoCount(0)
                .comment("test")
                .rating(5)
                .totalPrice(10000)
                .accounts(Collections.singleton(account))
                .videos(Collections.emptySet())
                .account(account)
                .videos(Collections.emptySet())
                .tags(Collections.emptySet())
                .questions(Collections.emptySet())
                .reviews(Collections.emptySet())
                .build();
        learningRepository.save(learning);
    }
}