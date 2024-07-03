package com.choidh.service.notice.service;

import com.choidh.service.notice.entity.Notice;
import com.choidh.service.notice.vo.NoticeDetailResult;
import com.choidh.service.notice.vo.NoticeListResult;
import com.choidh.service.notice.vo.NoticeVO;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    /**
     * 공지사항 페이징 (관리자)
     */
    NoticeListResult getNoticeListResultForAdmin(Pageable pageable);

    /**
     * 공지사항 페이징
     */
    NoticeListResult getNoticeListResultForPublic(Pageable pageable);

    /**
     * 공지사항 등록
     */
    Notice regNotice(NoticeVO noticeVO);

    /**
     * 공지사항 단건 조회
     */
    Notice getNoticeById(Long noticeId);

    /**
     * 공지사항 상세 조회
     */
    NoticeDetailResult getNoticeDetail(Long noticeId);

    /**
     * 공지사항 수정
     */
    void modNotice(NoticeVO noticeVO);

    /**
     * 공지사항 삭제
     */
    void delNotice(Long noticeId);
}
