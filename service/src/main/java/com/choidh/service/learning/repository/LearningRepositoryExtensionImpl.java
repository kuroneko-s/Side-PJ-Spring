package com.choidh.service.learning.repository;


import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.choidh.service.learning.entity.QLearning.learning;

public class LearningRepositoryExtensionImpl extends QuerydslRepositorySupport implements LearningRepositoryExtension {
    private final String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

    public LearningRepositoryExtensionImpl() {
        super(Learning.class);
    }

    /**
     * Learning 페이징
     */
    @Override
    public Page<Learning> findAllWithPageable(Pageable pageable) {
        JPQLQuery<Learning> query = from(learning)
                .where(learning.opening.isTrue());

        JPQLQuery<Learning> resultList = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return new PageImpl<>(resultList.fetch(), pageable, query.fetchCount());
    }

    /**
     * Learning 페이징. By Keyword
     */
    @Override
    public Page<Learning> findByKeywordWithPageable(String keyword, Pageable pageable) {
        keyword = keyword.replaceAll(match, "");

        Predicate learningWhere = learning.title.containsIgnoreCase(keyword)
                .or(learning.tags.any().tag.title.containsIgnoreCase(keyword));

        JPQLQuery<Learning> query = from(learning)
                .where(learning.opening.isTrue()
                                .and(learningWhere));

        List<Learning> resultList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(resultList, pageable, query.fetchCount());
    }

    /**
     * Learning 페이징. By 카테고리
     */
    @Override
    public Page<Learning> findByCategoryWithPageable(String category, Pageable pageable) {
        JPQLQuery<Learning> query = from(learning)
                .where(learning.opening.isTrue()
                        .and(learning.mainCategory.containsIgnoreCase(category)));

        List<Learning> learningList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(learningList, pageable, query.fetchCount());
    }

    /**
     * Learning 페이징. By 카레고리, Keyword
     */
    @Override
    public Page<Learning> findByCategoryAndKeywordWithPageable(String keyword, String category, Pageable pageable) {
        keyword = keyword.replaceAll(match, "");

        JPQLQuery<Learning> query = from(learning)
                .where(
                        learning.opening.isTrue(),
                        learning.mainCategory.eq(category),
                        learning.title.containsIgnoreCase(keyword)
                                .or(learning.tags.any().tag.title.containsIgnoreCase(keyword))
                )
                .distinct();

        List<Learning> learningList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(learningList, pageable, query.fetchCount());
    }

    /**
     * Learning 목록조회. By Tag 목록.
     */
    @Override
    public List<Learning> findTop12ByTagsOrderByRatingDesc(List<Tag> accountTagList) {
        return from(learning)
                .where(
                        learning.opening.isTrue(),
                        learning.tags.any().tag.in(accountTagList)
                )
                .orderBy(learning.rating.desc())
                .limit(12)
                .distinct()
                .fetch();
    }
}
