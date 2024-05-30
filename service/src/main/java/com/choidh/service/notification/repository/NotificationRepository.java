package com.choidh.service.notification.repository;


import com.choidh.service.account.entity.Account;
import com.choidh.service.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = "select count(*) " +
            "from Account a " +
            "where a = :account " +
            "and a.checked = :checked")
    long countByAccountAndChecked(Account account, boolean checked);

    @Query(value = "update from Notification n set n.used = false where n.id = :notificationId")
    void updateById(Long notificationId);

    @Query(value = "select n " +
            "from Notification n " +
            "where n.notificationType in :typeList " +
            "or n.learning in :learningIdList " +
            "and n.used = true")
    List<Notification> findListByType(List<String> typeList, List<Long> learningIdList);
}
