package com.choidh.service.notification.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.annotation.Name;
import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.notification.vo.NotificationType;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Notification extends BaseDateEntity {
    @Id @GeneratedValue
    private Long id;

    @Name(name = "강의 이름")
    private String lectureName;

    @Name(name = "알람 제목")
    private String title;

    @Name(name = "알람 내용")
    private String description;

    @Name(name = "알람 읽음 여부 표시")
    private boolean checked = false;

    @ManyToOne
    private Account account;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}
