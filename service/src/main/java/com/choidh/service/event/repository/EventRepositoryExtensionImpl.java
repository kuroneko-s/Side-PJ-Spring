package com.choidh.service.event.repository;


import com.choidh.service.event.entity.Event;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;

import static com.choidh.service.event.entity.QEvent.event;

public class EventRepositoryExtensionImpl extends QuerydslRepositorySupport implements EventRepositoryExtension {
    public EventRepositoryExtensionImpl() {
        super(Event.class);
    }

    /**
     * 이벤트 목록 페이징.
     */
    @Override
    public Page<Event> findListWithPaging(Pageable pageable) {
        JPQLQuery<Event> eventJPQLQuery = from(event)
                .where(event.used.isTrue());
        OrderSpecifier<LocalDateTime> orderSpecifier = event.createdAt.asc();

        for (Sort.Order order : pageable.getSort()) {
            Sort.Direction direction = order.getDirection();

            if (direction.isDescending()) {
                orderSpecifier = event.createdAt.desc();
            }
        }

        List<Event> eventList = eventJPQLQuery
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(eventList, pageable, eventJPQLQuery.fetchCount());
    }
}
