package com.choidh.service.notification.service;


import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.repository.NotificationRepository;
import com.choidh.service.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;

    @Override
    public void delNotification(Long notificationId) {
        notificationRepository.updateById(notificationId);
    }

    /**
     * 알람 목록조회 by LearningId
     */
    @Override
    public List<Notification> getNotificationListByType(List<Long> learningList) {
        List<String> typeList = new ArrayList<>();
        typeList.add(NotificationType.SITE.name());
        typeList.add(NotificationType.EVENT.name());

        return notificationRepository.findListByType(typeList, learningList);
    }


}
