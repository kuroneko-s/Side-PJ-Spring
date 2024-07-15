package com.choidh.web.notification.controller;

import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * Get 알림 상세 조회
     */
    @GetMapping("/{notificationId}")
    public String getNotificationDetailView(@PathVariable Long notificationId) {
        Notification notification = notificationService.getNotificationById(notificationId);

        switch (notification.getNotificationType()) {
            case SITE:
            case NOTICE:
                return "redirect:/notice/" + notification.getNotice().getId();
            case EVENT:
                return "redirect:/event/" + notification.getEvent().getId();
        }

        throw new IllegalArgumentException();
    }
}
