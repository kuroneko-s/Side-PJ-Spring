package com.choidh.service.notification.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.notification.repository.NotificationRepository;
import com.choidh.service.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("dev")
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class NotificationTest {
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;


    @Test
    public void create() throws Exception {
        Account account = accountRepository.getOne(98L);

        Notification notification = Notification.builder()
                .title("sample1")
                .lectureName("sample")
                .description("sample description")
                .checked(false)
                .account(account)
                .notificationType(NotificationType.CREATE)
                .build();

        notificationRepository.save(notification);
        account.getNotifications().add(notification);
    }

}