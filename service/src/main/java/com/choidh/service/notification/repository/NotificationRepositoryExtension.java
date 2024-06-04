package com.choidh.service.notification.repository;

import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.vo.NotificationType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface NotificationRepositoryExtension {
    /**
     * Notification 목록 조회. By Type is SITE, EVENT, NOTICE for Learning
     */
    List<Notification> findListByTypeAndLearning(List<NotificationType> types, List<Long> learningIdList);
}
