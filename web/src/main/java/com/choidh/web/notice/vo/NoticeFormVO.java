package com.choidh.web.notice.vo;

import com.choidh.service.notice.vo.NoticeType;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class NoticeFormVO {
    private Long noticeId;

    private String title;

    private String content;

    private NoticeType noticeType = NoticeType.WEB;

    private Long learningId;
}
