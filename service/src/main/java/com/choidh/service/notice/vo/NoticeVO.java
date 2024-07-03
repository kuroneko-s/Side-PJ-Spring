package com.choidh.service.notice.vo;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class NoticeVO {
    private Long noticeId;

    private String title;

    private String content;

    private NoticeType noticeType;

    private Long learningId;
}
