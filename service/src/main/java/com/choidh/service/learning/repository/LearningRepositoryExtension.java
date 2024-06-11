package com.choidh.service.learning.repository;

import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
public interface LearningRepositoryExtension {
    /**
     * Learning 페이징
     */
    Page<Learning> findAllWithPageable(Pageable pageable);

    /**
     * Learning 페이징. By Keyword
     */
    Page<Learning> findByKeywordWithPageable(String keyword, Pageable pageable);

    /**
     * Learning 페이징. By 카테고리
     */
    Page<Learning> findPagingByCategory(String mainCategory, Pageable pageable);

    /**
     * Learning 페이징. By 카테고리 And 키워드
     */
    Page<Learning> findPagingByCategoryAndKeyword(String mainCategory,String keyword, Pageable pageable);

    /**
     * Learning 페이징. By 카레고리, Keyword
     */
    Page<Learning> findByCategoryAndKeywordWithPageable(String keyword, String category, Pageable pageable);

    /**
     * Learning 목록 조회. By Tag 목록.
     */
    List<Learning> findTop12ByTagsOrderByRatingDesc(List<Tag> accountTagList);

    /**
     * Learning 목록 조회. Top 12 By Tags Order By 개설(openingDate) 일시 DESC.
     */
    List<Learning> findTop12LearningListByTagsOrderByOpeningDate(Set<Tag> tags);

    /**
     * Learning 목록 조회. Top 12 By Tags Order By Rating DESC
     */
    List<Learning> findTop12LearningListByTagsOrderByRating(Set<Tag> tags);
}
