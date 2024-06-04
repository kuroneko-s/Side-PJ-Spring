package com.choidh.service.notification.service;


import com.choidh.service.notification.entity.Notification;

import java.util.List;

public interface NotificationService {
    void delNotification(Long notificationId);

    /**
     * 알람 목록조회 By Learning Id
     */
    List<Notification> getNotificationListByType(List<Long> learningList);

    /**
     * 알람 갯수 조회 By Account Id
     */
    int getNotificationCountByAccount(Long accountId);
}
