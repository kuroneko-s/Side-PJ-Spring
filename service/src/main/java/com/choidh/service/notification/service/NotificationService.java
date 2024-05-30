package com.choidh.service.notification.service;


import com.choidh.service.notification.entity.Notification;

import java.util.List;

public interface NotificationService {
    void delNotification(Long notificationId);

    /**
     * 알람 목록조회 by LearningId
     */
    List<Notification> getNotificationListByType(List<Long> learningList);
}
