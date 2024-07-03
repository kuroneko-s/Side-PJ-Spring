package com.choidh.service.notice.service;

import com.choidh.service.common.pagination.Paging;
import com.choidh.service.joinTables.entity.LearningNoticeJoinTable;
import com.choidh.service.joinTables.service.LearningNoticeService;
import com.choidh.service.notice.entity.Notice;
import com.choidh.service.notice.repository.NoticeRepository;
import com.choidh.service.notice.vo.NoticeDetailResult;
import com.choidh.service.notice.vo.NoticeListResult;
import com.choidh.service.notice.vo.NoticeType;
import com.choidh.service.notice.vo.NoticeVO;
import com.choidh.service.notification.service.NotificationService;
import com.choidh.service.notification.vo.NotificationType;
import com.choidh.service.notification.vo.RegNotificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.choidh.service.common.AppConstant.getNoticeNotFoundErrorMessage;

@Service
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private LearningNoticeService learningNoticeService;
    @Autowired
    private NotificationService notificationService;

    /**
     * 공지사항 페이징 (관리자)
     */
    @Override
    public NoticeListResult getNoticeListResultForAdmin(Pageable pageable) {
        Page<Notice> noticePage = noticeRepository.findListWithPaging(pageable);

        String paginationUrl = "/admin/notice/list?sort=createdAt,asc&page=";
        for (Sort.Order order : pageable.getSort()) {
            Sort.Direction direction = order.getDirection();

            if (direction.isDescending()) {
                paginationUrl = "/admin/notice/list?sort=createdAt,desc&page=";
            }
        }

        Paging paging = Paging.builder()
                .hasNext(noticePage.hasNext())
                .hasPrevious(noticePage.hasPrevious())
                .number(noticePage.getNumber())
                .totalPages(Math.max(noticePage.getTotalPages() - 1, 0))
                .paginationUrl(paginationUrl)
                .build();

        return NoticeListResult.builder()
                .noticeList(noticePage.getContent())
                .paging(paging)
                .build();
    }

    /**
     * 공지사항 페이징
     */
    @Override
    public NoticeListResult getNoticeListResultForPublic(Pageable pageable) {
        Page<Notice> noticePage = noticeRepository.findListWithPaging(pageable);

        String paginationUrl = "/notice/list?sort=createdAt,asc&page=";
        for (Sort.Order order : pageable.getSort()) {
            Sort.Direction direction = order.getDirection();

            if (direction.isDescending()) {
                paginationUrl = "/notice/list?sort=createdAt,desc&page=";
            }
        }

        Paging paging = Paging.builder()
                .hasNext(noticePage.hasNext())
                .hasPrevious(noticePage.hasPrevious())
                .number(noticePage.getNumber())
                .totalPages(Math.max(noticePage.getTotalPages() - 1, 0))
                .paginationUrl(paginationUrl)
                .build();

        return NoticeListResult.builder()
                .noticeList(noticePage.getContent())
                .paging(paging)
                .build();
    }

    /**
     * 공지사항 등록
     */
    @Override
    @Transactional
    public Notice regNotice(NoticeVO noticeVO) {
        // 공지사항 등록
        Notice notice = noticeRepository.save(Notice.builder()
                .content(noticeVO.getContent())
                .title(noticeVO.getTitle())
                .noticeType(noticeVO.getNoticeType())
                .build());

        // 알림 등록 정보 생성
        RegNotificationVO regNotificationVO = RegNotificationVO.builder()
                .title(notice.getTitle())
                .description(notice.getContent())
                .used(true)
                .notificationType(NotificationType.SITE) // 사이트용 공지사항
                .notice(notice)
                .learning(null)
                .event(null)
                .build();

        // 강의에 대한 공지사항일 경우
        LearningNoticeJoinTable learningNoticeJoinTable;
        if (notice.getNoticeType() == NoticeType.LEARNING) {
            learningNoticeJoinTable = learningNoticeService.regLearningNoticeJoinTable(noticeVO.getLearningId(), notice.getId());
            regNotificationVO.setLearning(learningNoticeJoinTable.getLearning());
            regNotificationVO.setNotificationType(NotificationType.NOTICE); // 강의용 공지사항
        }

        // 알림 등록
        notificationService.regNotification(regNotificationVO);

        return notice;
    }

    /**
     * 공지사항 단건 조회
     */
    @Override
    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException(getNoticeNotFoundErrorMessage(noticeId)));
    }

    /**
     * 공지사항 상세 조회
     */
    @Override
    public NoticeDetailResult getNoticeDetail(Long noticeId) {
        Notice notice = this.getNoticeById(noticeId);

        return NoticeDetailResult.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .build();
    }

    /**
     * 공지사항 수정
     */
    @Override
    @Transactional
    public void modNotice(NoticeVO noticeVO) {
        Notice notice = this.getNoticeById(noticeVO.getNoticeId());
        notice.setTitle(noticeVO.getTitle());
        notice.setContent(noticeVO.getContent());
    }

    /**
     * 공지사항 삭제
     */
    @Override
    @Transactional
    public void delNotice(Long noticeId) {
        // 해당 공지사항에 대한 알림 삭제
        notificationService.delNotificationByNoticeId(noticeId);

        // 알림 삭제
        noticeRepository.delNotice(noticeId);
    }
}
