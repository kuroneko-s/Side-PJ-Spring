package com.choidh.service.account.vo;

import com.choidh.service.annotation.Name;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModNotificationVO {
    @Name(name = "사이트 관련 메일 알림 활성화 여부", description = "라고 생각함")
    private boolean siteMailNotification;

    @Name(name = "사이트 관련 사이트 내 알림 활성화 여부", description = "라고 생각함")
    private boolean siteWebNotification;

    @Name(name = "강의 관련 메일 알림 활성화 여부", description = "라고 생각함")
    private boolean learningMailNotification;

    @Name(name = "강의 관련 사이트 내 알림 활성화 여부", description = "라고 생각함")
    private boolean learningWebNotification;
}
