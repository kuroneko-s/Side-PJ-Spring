package com.choidh.web.notification.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;

    @Override
    public void readNotifications(List<Notification> notCheckedNotifications) {
        for (Notification notification : notCheckedNotifications) {
            notification.setChecked(true);
        }
    }

    @Override
    public void deleteNotifications(Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
    }
}
