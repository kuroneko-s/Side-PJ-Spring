package com.choidh.service.event.service;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.event.entity.Event;
import com.choidh.service.event.vo.EventListResult;
import com.choidh.service.event.vo.RegEventVO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    /**
     * 이벤트 목록 조회.
     */
    List<Event> getEventList();

    /**
     * 이벤트 목록 페이징.
     */
    EventListResult getEventListResult(Pageable pageable);

    /**
     * 이벤트 목록 조회 With 첨부 파일.
     */
    List<AttachmentFile> getEventFileList();

    /**
     * 이벤트 생성.
     */
    Event regEvent(RegEventVO regEventVO);
}
