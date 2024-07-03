package com.choidh.service.notification.eventListener;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountPredicates;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.common.ServiceAppProperties;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.mail.vo.EmailMessageVO;
import com.choidh.service.notice.entity.Notice;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.eventListener.vo.LearningClosedEvent;
import com.choidh.service.notification.eventListener.vo.LearningCreateEvent;
import com.choidh.service.notification.eventListener.vo.LearningUpdateEvent;
import com.choidh.service.notification.repository.NotificationRepository;
import com.choidh.service.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Async
@Component
@RequiredArgsConstructor
public class NotificationLearningEventListener {
    private final AccountRepository accountRepository;
    private final LearningRepository learningRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final ServiceAppProperties serviceAppProperties;
    private final AccountPredicates accountPredicates;

    @EventListener
    public void learningCreateListener(LearningCreateEvent event) {
        Optional<Learning> learning = learningRepository.findById(event.getLearning().getId());
        Learning newLearning = learning.orElseThrow();
        Set<Long> tags = newLearning.getTags().stream().map(LearningTagJoinTable::getId).collect(Collectors.toSet());

        Iterable<Account> accounts = accountRepository.findAll(accountPredicates.findByTags(tags));
        createNotification(newLearning, null, "강의가 새롭게 생성되었습니다.", NotificationType.NOTICE);

        for (Account account : accounts) {
            if (account.isSiteMailNotification())
                sendEmail(account, "강의가 새롭게 생성되었습니다.", "/learning/" + newLearning.getId(), newLearning.getTitle(), "강의 생성 알림");
        }
    }

    @EventListener
    public void learningClosedListener(LearningClosedEvent event) {
        Optional<Learning> learning = learningRepository.findById(event.getLearning().getId());
        Learning newLearning = learning.orElseThrow();
        Set<Long> tags = newLearning.getTags().stream().map(LearningTagJoinTable::getId).collect(Collectors.toSet());

        Iterable<Account> accounts = accountRepository.findAll(accountPredicates.findByTags(tags));
        createNotification(newLearning, null, "강의가 종료되었습니다.", NotificationType.NOTICE);

        for (Account account : accounts) {
            if (account.isSiteMailNotification())
                sendEmail(account, "강의가 종료되었습니다.", "/learning/" + newLearning.getId(), newLearning.getTitle(), "강의 종료 알림");
        }
    }

    // 강의 갱신 시 동작하는 이벤트 리스너.
    @EventListener
    public void learningUpdateListener(LearningUpdateEvent event) {
        Optional<Learning> learning = learningRepository.findById(event.getLearning().getId());
        Learning newLearning = learning.orElseThrow();
        Set<Long> tags = newLearning.getTags().stream().map(LearningTagJoinTable::getId).collect(Collectors.toSet());

        Iterable<Account> accounts = accountRepository.findAll(accountPredicates.findByTags(tags));
        createNotification(newLearning, null, "강의 내용이 갱신되었습니다.", NotificationType.NOTICE);

        for (Account account : accounts) {
            if (account.isSiteMailNotification())
                sendEmail(account, "강의 내용이 갱신되었습니다.", "/learning/" + newLearning.getId(), newLearning.getTitle(), "강의 변경 알림");
        }
    }

    private void sendEmail(Account account, String message, String url, String learningTitle, String subject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", message);
        context.setVariable("host", serviceAppProperties.getHost());
        context.setVariable("link", url);
        context.setVariable("linkName", learningTitle);

        String process = templateEngine.process("mail/notificationmail", context);

        EmailMessageVO emailMessageVO = EmailMessageVO.builder()
                .to(account.getEmail())
                .subject(subject)
                .message(process)
                .build();

        emailService.sendEmail(emailMessageVO);
    }

    private void createNotification(Learning learning, Notice notice, String message, NotificationType type) {
        notificationRepository.save(Notification.builder()
                .title(learning.getTitle() + " " + message)
                .description(learning.getSimpleSubscription())
                .used(true)
                .notificationType(type)
                .learning(learning)
                .notice(notice)
                .build());
    }
}
