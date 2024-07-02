package com.choidh.service.event.service;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.event.entity.Event;
import com.choidh.service.event.vo.EventDetailResult;
import com.choidh.service.event.vo.EventListResult;
import com.choidh.service.event.vo.EventVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
     Event regEvent(EventVO eventVO, Map<String, MultipartFile> fileMap);

    /**
     * 이벤트 단건 조회
     */
    EventDetailResult getEventDetail(Long eventId);

    /**
     * 이벤트 수정.
     */
    Event modEvent(EventVO eventVO, Map<String, MultipartFile> fileMap);

    /**
     * 이벤트 삭제
     */
    void delEvent(Long eventId);
}
