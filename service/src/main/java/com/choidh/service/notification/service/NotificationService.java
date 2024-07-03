package com.choidh.service.notification.service;


import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.vo.RegNotificationVO;

import java.util.List;

public interface NotificationService {
    /**
     * 알림 삭제 By Notice Id
     */
    void delNotificationByNoticeId(Long noticeId);

    /**
     * 알림 삭제
     */
    void delNotification(Long notificationId);

    /**
     * 알람 목록조회. By Account
     */
    List<Notification> getNotificationListByType(Long accountId);

    /**
     * 알람 갯수 조회 By Account Id
     */
    int getNotificationCountByAccount(Long accountId);

    /**
     * 알림 추가.
     */
    Notification regNotification(RegNotificationVO regNotificationVO);

    /**
     * 알림 상세 조회. By Notification Id
     */
    Notification getNotificationById(Long notificationId);
}
