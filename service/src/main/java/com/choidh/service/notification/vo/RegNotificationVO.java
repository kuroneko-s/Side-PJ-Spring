package com.choidh.service.notification.vo;

import com.choidh.service.event.entity.Event;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.notice.entity.Notice;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RegNotificationVO {
    private String title;

    private String description;

    private boolean used = true;

    private NotificationType notificationType;

    private Learning learning;

    private Notice notice;

    private Event event;
}
