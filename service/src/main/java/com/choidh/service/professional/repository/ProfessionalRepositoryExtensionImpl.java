package com.choidh.service.professional.repository;


import com.choidh.service.professional.entity.ProfessionalAccount;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;

import static com.choidh.service.professional.entity.QProfessionalAccount.professionalAccount;

public class ProfessionalRepositoryExtensionImpl extends QuerydslRepositorySupport implements ProfessionalRepositoryExtension {
    public ProfessionalRepositoryExtensionImpl() {
        super(ProfessionalAccount.class);
    }

    @Override
    public Page<ProfessionalAccount> findByList(Pageable pageable) {
        JPQLQuery<ProfessionalAccount> eventJPQLQuery = from(professionalAccount);
        OrderSpecifier<LocalDateTime> orderSpecifier = professionalAccount.createdAt.asc();

        for (Sort.Order order : pageable.getSort()) {
            Sort.Direction direction = order.getDirection();

            if (direction.isDescending()) {
                orderSpecifier = professionalAccount.createdAt.desc();
            }
        }

        List<ProfessionalAccount> professionalAccountList = eventJPQLQuery
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(professionalAccountList, pageable, eventJPQLQuery.fetchCount());

    }
}
