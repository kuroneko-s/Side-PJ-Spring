package com.choidh.service.learning.repository;


import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.entity.QLearning;
import com.choidh.service.tag.entity.Tag;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class LearningRepositoryExtensionImpl extends QuerydslRepositorySupport implements LearningRepositoryExtension{

    public LearningRepositoryExtensionImpl() {
        super(Learning.class);
    }

    @Override
    public Page<Learning> findByKeywordWithPageable(String keyword, Pageable pageable) {
        QLearning learning = QLearning.learning;

        JPQLQuery<Learning> query = from(learning).where(learning.opening
                .and(learning.title.containsIgnoreCase(keyword))
                .or(learning.tags.any().tag.title.containsIgnoreCase(keyword)))
                .distinct();
        JPQLQuery<Learning> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Learning> pageableQueryResult = pageableQuery.fetchResults();

        return new PageImpl<>(pageableQueryResult.getResults(), pageable, pageableQueryResult.getTotal());
    }

    @Override
    public Page<Learning> findAllWithPageable(Boolean tf, Pageable pageable) {
        //.distinct() 중복값 제거

        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> query = from(learning).where(learning.opening);
        JPQLQuery<Learning> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Learning> pageableQueryResult = pageableQuery.fetchResults();

        return new PageImpl<>(pageableQueryResult.getResults(), pageable, pageableQueryResult.getTotal());
    }

    @Override
    public Page<Learning> findByKategorieWithPageable(boolean b, String kategorie, Pageable pageable) {
        int index = kategorie.equalsIgnoreCase("web") ? 1 : 2;
        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> query = from(learning).where(learning.opening
                .and(learning.mainCategory.eq(index+"")));
        JPQLQuery<Learning> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Learning> pageableQueryResult = pageableQuery.fetchResults();

        return new PageImpl<>(pageableQueryResult.getResults(), pageable, pageableQueryResult.getTotal());
    }

    @Override
    public Page<Learning> findByKategorieAndKeywordWithPageable(boolean b, String keyword, String kategorie, Pageable pageable) {
        int index = kategorie.equalsIgnoreCase("web") ? 1 : 2;
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z]";
        keyword = keyword.replaceAll(match, "");
        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> query = from(learning).where(learning.opening
                .and(learning.mainCategory.eq(index + ""))
                .and(learning.title.containsIgnoreCase(keyword))
                .or(learning.tags.any().tag.title.containsIgnoreCase(keyword)))
                .distinct();
        JPQLQuery<Learning> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Learning> pageableQueryResult = pageableQuery.fetchResults();

        return new PageImpl<>(pageableQueryResult.getResults(), pageable, pageableQueryResult.getTotal());
    }

    /*@Override
    public List<Learning> findTop4ByTagsOrderByRatingDesc(Set<Tag> tags) {
        Predicate top4ByTags = LearningPredicates.findTop4ByTagIds(tags);
        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> distinct;

        if (top4ByTags == null) {
            distinct = from(learning).where(learning.startingLearning.isTrue()
                    .and(learning.closedLearning.isFalse())).limit(4).distinct();
        }else {
            distinct = from(learning).where(learning.startingLearning.isTrue()
                    .and(learning.closedLearning.isFalse())
                    .and(LearningPredicates.findTop4ByTagIds(tags)))
                    .leftJoin(learning.tags, QTag.tag).fetchJoin()
                    .limit(4)
                    .distinct();
        }

        return distinct.fetch();
    }*/

    @Override
    public List<Learning> findTop12ByTagsOrderByRatingDesc(List<Tag> accountTagList) {
        QLearning learning = QLearning.learning;

        return from(learning)
                .where(
                        learning.opening.isTrue()
                                .and(learning.tags.any().tag.in(accountTagList))
                )
                .limit(12)
                .distinct()
                .fetch();
    }
}
