package com.choidh.service.notification.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.notification.vo.NotificationType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Notification {
    @Id @GeneratedValue
    private Long id;

    private String title;

    private String lectureName;

    private String description;

    private boolean checked = false;

    @ManyToOne
    private Account account;

    private LocalDateTime createNotification;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
