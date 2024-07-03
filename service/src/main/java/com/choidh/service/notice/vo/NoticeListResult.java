package com.choidh.service.notice.vo;

import com.choidh.service.common.pagination.Paging;
import com.choidh.service.notice.entity.Notice;
import lombok.*;

import java.util.List;

@Getter @Setter
@Builder @NoArgsConstructor
@AllArgsConstructor
public class NoticeListResult {
    private List<Notice> noticeList;

    private Paging paging;
}
