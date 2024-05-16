package com.choidh.service.notification.repository;


import com.choidh.service.account.entity.Account;
import com.choidh.service.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean check);

    List<Notification> findByAccountAndChecked(Account account, boolean checked);

    void deleteByAccountAndChecked(Account account, boolean b);
}
