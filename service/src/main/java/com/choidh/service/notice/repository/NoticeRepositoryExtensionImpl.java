package com.choidh.service.notice.repository;


import com.choidh.service.notice.entity.Notice;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;

import static com.choidh.service.notice.entity.QNotice.notice;

public class NoticeRepositoryExtensionImpl extends QuerydslRepositorySupport implements NoticeRepositoryExtension {
    public NoticeRepositoryExtensionImpl() {
        super(Notice.class);
    }

    /**
     * 공지사항 페이징.
     */
    @Override
    public Page<Notice> findListWithPaging(Pageable pageable) {
        JPQLQuery<Notice> eventJPQLQuery = from(notice);
        OrderSpecifier<LocalDateTime> orderSpecifier = notice.createdAt.asc();

        for (Sort.Order order : pageable.getSort()) {
            Sort.Direction direction = order.getDirection();

            if (direction.isDescending()) {
                orderSpecifier = notice.createdAt.desc();
            }
        }

        List<Notice> noticeList = eventJPQLQuery
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(noticeList, pageable, eventJPQLQuery.fetchCount());
    }
}
