package com.choidh.web.notification.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.service.NotificationService;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/notification")
@Controller
public class NotificationController {
    @Autowired private NotificationService notificationService;

    @GetMapping("/{notificationId}")
    public String getNotificationDetailView(@CurrentAccount Account account, Model model, @PathVariable String notificationId) {
        Notification notification = notificationService.getNotificationById(Long.valueOf(notificationId));

        switch (notification.getNotificationType()) {
            case NOTICE:
                return "redirect:/notice/" + notification.getNotice().getId();
            case SITE:
            case EVENT:
                return "redirect:/event/" + notification.getEvent().getId();
        }

        throw new IllegalArgumentException();
    }
}
