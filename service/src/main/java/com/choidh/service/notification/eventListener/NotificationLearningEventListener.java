package com.choidh.service.notification.eventListener;


import com.choidh.service.account.entity.Account;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.joinTables.service.AccountTagService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.mail.vo.EmailMessageVO;
import com.choidh.service.mail.vo.NotificationMailType;
import com.choidh.service.notification.eventListener.vo.LearningClosedEvent;
import com.choidh.service.notification.eventListener.vo.LearningCreateEvent;
import com.choidh.service.notification.eventListener.vo.LearningUpdateEvent;
import com.choidh.service.notification.service.NotificationService;
import com.choidh.service.notification.vo.NotificationType;
import com.choidh.service.notification.vo.RegNotificationVO;
import com.choidh.service.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Async
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationLearningEventListener {
    private final LearningService learningService;
    private final NotificationService notificationService;
    private final AccountTagService accountTagService;
    private final EmailService emailService;

    @EventListener
    public void learningCreateListener(LearningCreateEvent event) {
        Learning learning = learningService.getLearningById(event.getLearning().getId());
        Set<Tag> tags = learning.getTags().stream().map(LearningTagJoinTable::getTag).collect(Collectors.toSet());
        Set<AccountTagJoinTable> accountListByTagIds = accountTagService.getAccountListByTagIds(tags);

        notificationService.regNotification(RegNotificationVO.builder()
                        .title(learning.getTitle() + " 강의가 새롭게 생성되었습니다.")
                        .description(learning.getSimpleSubscription())
                        .notificationType(NotificationType.LEARNING_CREATE)
                        .learning(learning)
                        .used(true)
                .build());

        for (AccountTagJoinTable accountTagJoinTable : accountListByTagIds) {
            Account account = accountTagJoinTable.getAccount();
            if (account.isSiteMailNotification())
                emailService.sendEmail(EmailMessageVO.getInstanceForLearning(account, learning, NotificationMailType.CREATE));
        }
    }

    // 강의 갱신 시 동작하는 이벤트 리스너.
    @EventListener
    public void learningUpdateListener(LearningUpdateEvent event) {
        Learning learning = learningService.getLearningById(event.getLearning().getId());
        Set<Tag> tags = learning.getTags().stream().map(LearningTagJoinTable::getTag).collect(Collectors.toSet());
        Set<AccountTagJoinTable> accountListByTagIds = accountTagService.getAccountListByTagIds(tags);

        notificationService.regNotification(RegNotificationVO.builder()
                .title(learning.getTitle() + " 강의 내용이 갱신되었습니다.")
                .description(learning.getSimpleSubscription())
                .notificationType(NotificationType.LEARNING_UPDATE)
                .learning(learning)
                .used(true)
                .build());

        for (AccountTagJoinTable accountTagJoinTable : accountListByTagIds) {
            Account account = accountTagJoinTable.getAccount();
            if (account.isSiteMailNotification())
                emailService.sendEmail(EmailMessageVO.getInstanceForLearning(account, learning, NotificationMailType.UPDATE));
        }
    }

    @EventListener
    public void learningClosedListener(LearningClosedEvent event) {
        Learning learning = learningService.getLearningById(event.getLearning().getId());
        Set<Tag> tags = learning.getTags().stream().map(LearningTagJoinTable::getTag).collect(Collectors.toSet());
        Set<AccountTagJoinTable> accountListByTagIds = accountTagService.getAccountListByTagIds(tags);

        notificationService.regNotification(RegNotificationVO.builder()
                .title(learning.getTitle() + " 강의가 종료되었습니다.")
                .description(learning.getSimpleSubscription())
                .notificationType(NotificationType.LEARNING_UPDATE)
                .learning(learning)
                .used(true)
                .build());

        for (AccountTagJoinTable accountTagJoinTable : accountListByTagIds) {
            Account account = accountTagJoinTable.getAccount();
            if (account.isSiteMailNotification())
                emailService.sendEmail(EmailMessageVO.getInstanceForLearning(account, learning, NotificationMailType.CLOSE));
        }
    }
}
