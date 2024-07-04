package com.choidh.service.event.repository;

import com.choidh.service.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryExtension {
    /**
     * 이벤트 목록 페이징.
     */
    Page<Event> findListWithPaging(Pageable pageable);
}
