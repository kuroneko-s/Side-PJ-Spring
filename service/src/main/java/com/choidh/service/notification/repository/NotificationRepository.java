package com.choidh.service.notification.repository;


import com.choidh.service.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryExtension {
    /**
     * Notification 비활성화. By Notification Id
     */
    @Query(value = "update from Notification n " +
            "set n.used = false " +
            "where n.id = :notificationId")
    @Modifying
    int updateById(Long notificationId);
}
