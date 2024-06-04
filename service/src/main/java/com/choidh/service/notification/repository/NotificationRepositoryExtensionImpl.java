package com.choidh.service.notification.repository;

import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.vo.NotificationType;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

import static com.choidh.service.notification.entity.QNotification.notification;

public class NotificationRepositoryExtensionImpl extends QuerydslRepositorySupport implements NotificationRepositoryExtension {
    public NotificationRepositoryExtensionImpl() {
        super(Notification.class);
    }

    /**
     * Notification 목록 조회. By Type is SITE, EVENT, NOTICE for Learning
     */
    @Override
    public List<Notification> findListByTypeAndLearning(List<NotificationType> types, List<Long> learningIdList) {
        Predicate predicate = null;

        List<NotificationType> typeList = new ArrayList<>(types);

        if (types.contains(NotificationType.NOTICE)) {
            predicate = notification.notificationType.eq(NotificationType.NOTICE)
                    .and(notification.learning.id.in(learningIdList));
            typeList.remove(NotificationType.NOTICE);
        }

        return from(notification)
                .where(
                        notification.used.isTrue()
                                .and(
                                        notification.notificationType.in(typeList)
                                                .or(predicate)
                                )
                )
                .leftJoin(notification.notice).fetchJoin()
                .leftJoin(notification.learning).fetchJoin()
                .fetch();
    }
}
