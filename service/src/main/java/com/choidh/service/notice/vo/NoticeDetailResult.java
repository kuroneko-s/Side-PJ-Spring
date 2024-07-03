package com.choidh.service.notice.vo;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class NoticeDetailResult {
    private Long noticeId;

    private String title;

    private String content;
}
