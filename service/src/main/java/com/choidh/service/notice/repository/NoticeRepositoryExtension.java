package com.choidh.service.notice.repository;

import com.choidh.service.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface NoticeRepositoryExtension {
    /**
     * 공지사항 페이징.
     */
    Page<Notice> findListWithPaging(Pageable pageable);
}
