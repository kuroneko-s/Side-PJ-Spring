package com.choidh.service.notification.entity;

import com.choidh.service.annotation.Name;
import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.notice.entity.Notice;
import com.choidh.service.notification.vo.NotificationType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Notification extends BaseDateEntity {
    @Id @GeneratedValue
    private Long id;

    @Name(name = "알람 제목")
    private String title;

    @Name(name = "알람 내용")
    private String description;

    @Name(name = "알람 유효 여부")
    private boolean used = true;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @OneToOne(fetch = FetchType.LAZY)
    private Learning learning;

    @OneToOne(fetch = FetchType.LAZY)
    private Notice notice;
}
