package com.choidh.service.notification.repository;

import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.vo.NotificationType;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.choidh.service.notification.entity.QNotification.notification;

public class NotificationRepositoryExtensionImpl extends QuerydslRepositorySupport implements NotificationRepositoryExtension {
    public NotificationRepositoryExtensionImpl() {
        super(Notification.class);
    }

    /**
     * Notification 목록 조회. By Type is SITE, EVENT, NOTICE for Learning
     */
    @Override
    public List<Notification> findListByTypeAndLearning(List<NotificationType> types, List<Long> learningIdList, Set<LearningTagJoinTable> learningTags) {
        Predicate predicate = null;
        Predicate subPredicate = null;

        List<NotificationType> typeList = new ArrayList<>(types);

        if (types.contains(NotificationType.NOTICE)) {
            predicate = notification.notificationType.eq(NotificationType.NOTICE)
                    .and(notification.learning.id.in(learningIdList));
            typeList.remove(NotificationType.NOTICE);
        }

        if (types.contains(NotificationType.LEARNING_CREATE)) {
            subPredicate = notification.notificationType.eq(NotificationType.LEARNING_CREATE)
                    .and(notification.learning.tags.any().in(learningTags));
        }

        return from(notification)
                .where(
                        notification.used.isTrue()
                                .and(
                                        notification.notificationType.in(typeList)
                                                .or(predicate)
                                                .or(subPredicate)
                                )
                )
                .leftJoin(notification.notice).fetchJoin()
                .leftJoin(notification.learning).fetchJoin()
                .orderBy(notification.createdAt.desc())
                .fetch();
    }
}
