package com.choidh.web.notification.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.notification.entity.Notification;

import java.util.List;

public interface NotificationService {
    void readNotifications(List<Notification> notCheckedNotifications);

    void deleteNotifications(Account account);
}
