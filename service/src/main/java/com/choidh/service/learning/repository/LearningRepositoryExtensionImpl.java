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
import java.util.Set;

import static com.choidh.service.common.utiles.StringUtils.isNotNullAndEmpty;
import static com.choidh.service.common.utiles.StringUtils.isNullOrEmpty;
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
     * Learning 페이징. By Main Category
     */
    @Override
    public Page<Learning> findPagingByCategory(String mainCategory, Pageable pageable) {
        Predicate learningWhere = learning.opening.isTrue();

        if (isNotNullAndEmpty(mainCategory)) {
            learningWhere = learning.opening.isTrue().and(learning.mainCategory.containsIgnoreCase(mainCategory));
        }

        JPQLQuery<Learning> query = from(learning)
                .where(learningWhere);

        List<Learning> learningList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(learningList, pageable, query.fetchCount());
    }

    /**
     * Learning 페이징. By 카테고리 And 키워드
     */
    @Override
    public Page<Learning> findPagingByCategoryAndKeyword(String mainCategory, String keyword, Pageable pageable) {
        Predicate learningWhere = learning.opening.isTrue();

        if (isNotNullAndEmpty(keyword) && isNullOrEmpty(mainCategory)) {
            learningWhere = learning.opening.isTrue()
                    .and(learning.title.containsIgnoreCase(keyword).or(learning.tags.any().tag.title.containsIgnoreCase(keyword)));
        }
        else if (isNotNullAndEmpty(mainCategory) && isNullOrEmpty(keyword)) {
            learningWhere = learning.opening.isTrue().and(learning.mainCategory.containsIgnoreCase(mainCategory));
        }
        else if (isNotNullAndEmpty(mainCategory) && isNotNullAndEmpty(keyword)) {
            learningWhere = learning.opening.isTrue()
                    .and(learning.title.containsIgnoreCase(keyword).or(learning.tags.any().tag.title.containsIgnoreCase(keyword)))
                    .and(learning.mainCategory.containsIgnoreCase(mainCategory));
        }

        JPQLQuery<Learning> query = from(learning)
                .where(learningWhere);

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

    /**
     * Learning 목록 조회. Top 12 By Tags Order By 개설(openingDate) 일시 DESC.
     */
    @Override
    public List<Learning> findTop12LearningListByTagsOrderByOpeningDate(Set<Tag> tags) {
        return from(learning)
                .where(learning.opening.isTrue(),
                        learning.tags.any().tag.in(tags)
                )
                .orderBy(learning.openingDate.desc())
                .limit(12)
                .distinct()
                .fetch();
    }

    /**
     * Learning 목록 조회. Top 12 By Tags Order By Rating DESC
     */
    @Override
    public List<Learning> findTop12LearningListByTagsOrderByRating(Set<Tag> tags) {
        return from(learning)
                .where(learning.opening.isTrue(),
                        learning.tags.any().tag.in(tags))
                .orderBy(learning.rating.desc())
                .limit(12)
                .distinct()
                .fetch();
    }
}
