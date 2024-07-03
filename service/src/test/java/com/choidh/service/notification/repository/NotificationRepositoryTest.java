package com.choidh.service.notification.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.notice.entity.Notice;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
//@Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class NotificationRepositoryTest extends AbstractRepositoryTestConfig {
    private final NotificationRepository notificationRepository;

    private Notification createNotification(String title,
                                            String description,
                                            NotificationType notificationType,
                                            Learning learning,
                                            Notice notice) {
        return notificationRepository.save(Notification.builder()
                .title(title)
                .description(description)
                .used(true)
                .notificationType(notificationType)
                .learning(learning)
                .notice(notice)
                .build());
    }

    @Test
    @DisplayName("Notification 비활성화. By Notification Id")
    public void updateById() throws Exception {
        Account testAccount = createAccount();
        ProfessionalAccount testProfessionalAccount = createProfessionalAccount(testAccount);
        Learning learning = createLearning(testProfessionalAccount);
        Notification target = null;

        for (int i = 0; i < 7; i++) {
            Notification notification = createNotification(
                    "sample notification " + i,
                    "sample description " + i,
                    NotificationType.NOTICE,
                    learning,
                    null
            );

            if (i == 5) target = notification;
        }

        theLine();

        int updateResult = notificationRepository.updateById(target.getId());
        assertEquals(updateResult, 1);

        Notification notification = notificationRepository.findById(target.getId()).get();
        assertFalse(notification.isUsed());
    }

    @Test
    @DisplayName("Notification 목록 조회. By Type is SITE, EVENT, NOTICE for Learning")
    public void findListByType() throws Exception {
        Account testAccount = createAccount();
        ProfessionalAccount testProfessionalAccount = createProfessionalAccount(testAccount);
        Learning learning = createLearning(testProfessionalAccount);

        for (int i = 0; i < 9; i++) {
            Notification notification = createNotification(
                    "sample notification " + i,
                    "sample description " + i,
                    NotificationType.EVENT,
                    null,
                    null
            );
        }

        for (int i = 0; i < 3; i++) {
            Notification notification = createNotification(
                    "sample notification " + i,
                    "sample description " + i,
                    NotificationType.SITE,
                    null,
                    null
            );
        }

        for (int i = 0; i < 7; i++) {
            Notification notification = createNotification(
                    "sample notification " + i,
                    "sample description " + i,
                    NotificationType.NOTICE,
                    learning,
                    null
            );
        }

        List<NotificationType> typeList = List.of(NotificationType.NOTICE, NotificationType.SITE, NotificationType.EVENT);
        List<Notification> notificationList = notificationRepository.findListByTypeAndLearning(typeList, List.of(learning.getId()));
        assertEquals(notificationList.size(), 19);
    }

    @Test
    @DisplayName("Notification 목록 조회. By Type is SITE, EVENT, NOTICE for Learning_2")
    public void findListByType_2() throws Exception {
        Account testAccount = createAccount();
        ProfessionalAccount testProfessionalAccount = createProfessionalAccount(testAccount);
        Learning learning = createLearning(testProfessionalAccount);

        for (int i = 0; i < 9; i++) {
            Notification notification = createNotification(
                    "sample notification " + i,
                    "sample description " + i,
                    NotificationType.EVENT,
                    null,
                    null
            );
        }

        for (int i = 0; i < 3; i++) {
            Notification notification = createNotification(
                    "sample notification " + i,
                    "sample description " + i,
                    NotificationType.SITE,
                    null,
                    null
            );
        }

        for (int i = 0; i < 7; i++) {
            Notification notification = createNotification(
                    "sample notification " + i,
                    "sample description " + i,
                    NotificationType.NOTICE,
                    learning,
                    null
            );
        }

        List<NotificationType> typeList = List.of(NotificationType.NOTICE, NotificationType.SITE, NotificationType.EVENT);
        List<Notification> notificationList = notificationRepository.findListByTypeAndLearning(typeList, List.of());
        assertEquals(notificationList.size(), 12);
    }
}