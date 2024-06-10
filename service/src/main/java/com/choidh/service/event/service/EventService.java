package com.choidh.service.event.service;

import com.choidh.service.attachment.entity.AttachmentFile;

import java.util.List;

public interface EventService {
    /**
     * 이벤트 목록 조회 With 첨부 파일.
     */
    List<AttachmentFile> getEventFileList();
}
