package com.choidh.service.learning.repository;

import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    Page<Learning> findByCategoryWithPageable(String category, Pageable pageable);

    /**
     * Learning 페이징. By 카레고리, Keyword
     */
    Page<Learning> findByCategoryAndKeywordWithPageable(String keyword, String category, Pageable pageable);

    /**
     * Learning 페이징. By Tag 목록.
     */
    List<Learning> findTop12ByTagsOrderByRatingDesc(List<Tag> accountTagList);
}
