package com.choidh.web.notification.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.repository.NotificationRepository;
import com.choidh.service.notification.service.NotificationService;
import com.choidh.service.notification.vo.NotificationType;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
class NotificationControllerTest extends AbstractControllerTestConfig {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AccountService accountService;

    @Test
    @DisplayName("Get 알림 상세 조회 (비로그인)")
    public void getNotificationDetailViewNoneLogin() throws Exception {
        List<Notification> notificationList = notificationRepository.findAll().stream().filter(notification -> notification.getNotificationType() == NotificationType.SITE).collect(Collectors.toList());
        assertFalse(notificationList.isEmpty());
        Notification notification = notificationList.get(0);

        mockMvc.perform(get("/notification/" + notification.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Get 알림 상세 조회 (사이트 알림)")
    public void getEmailReAuthenticationSITEView() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        List<Notification> notificationListByType = notificationService.getNotificationListByType(account.getId());
        List<Notification> notificationList = notificationListByType.stream().filter(notification -> notification.getNotificationType() == NotificationType.SITE).collect(Collectors.toList());
        assertFalse(notificationList.isEmpty());
        Notification notification = notificationList.get(0);

        mockMvc.perform(get("/notification/" + notification.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notice/" + notification.getNotice().getId()));
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Get 알림 상세 조회 (이벤트 알림)")
    public void getEmailReAuthenticationEVENTView() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        List<Notification> notificationListByType = notificationService.getNotificationListByType(account.getId());
        List<Notification> notificationList = notificationListByType.stream().filter(notification -> notification.getNotificationType() == NotificationType.EVENT).collect(Collectors.toList());
        assertFalse(notificationList.isEmpty());
        Notification notification = notificationList.get(0);

        mockMvc.perform(get("/notification/" + notification.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/event/" + notification.getEvent().getId()));
    }

}