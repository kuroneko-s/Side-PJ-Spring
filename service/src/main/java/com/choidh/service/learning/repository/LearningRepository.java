package com.choidh.service.learning.repository;

import com.choidh.service.learning.entity.Learning;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@EnableJpaRepositories
public interface LearningRepository extends JpaRepository<Learning, Long>, QuerydslPredicateExecutor<Learning>, LearningRepositoryExtension {
    /**
     * Learning 단건 조회. By Learning PK
     */
    @Query(value = "select l " +
            "from Learning l " +
            "where l.id = :learningId")
    @EntityGraph(attributePaths = "tags", type = EntityGraph.EntityGraphType.LOAD)
    Learning findByIdWithTags(Long learningId);

    /**
     * Learning 목록조회. By Learning PK
     */
    @Query(value = "select l " +
            "from Learning l " +
            "where l IN :learningList")
    @EntityGraph(attributePaths = "tags", type = EntityGraph.EntityGraphType.LOAD)
    List<Learning> findListByLearningIdsWithTags(List<Learning> learningList);

    /**
     * Learning 목록조회 Limit 4. By Opening is true and OpningDate DESC 정렬
     */
    List<Learning> findTop4ByOpeningOrderByOpeningDateDesc(boolean opening);

    /**
     * Learning 목록조회 Limit 12. By Opening is true and Rating DESC 정렬
     */
    List<Learning> findTop12ByOpeningOrderByRatingDesc(boolean opening);

    /**
     * Learning 상세 조회. By Learning PK
     */
    @Query(value = "select l " +
            "from Learning l " +
            "where l.id = :learningId")
    @EntityGraph(attributePaths = {"noticesList", "carts", "purchaseHistories", "reviews", "questions", "tags"}, type = EntityGraph.EntityGraphType.LOAD)
    Learning findLearningDetailById(Long learningId);

    /**
     * Learning 단건 조회 By Learning PK With Question
     */
    @Query(value = "select l " +
            "from Learning l " +
            "where l.id = :learningId")
    @EntityGraph(attributePaths = "questions", type = EntityGraph.EntityGraphType.LOAD)
    Learning findLearningByIdWithQuestions(Long learningId);

    /**
     * Learning 목록 조회 By Learning PKs
     */
    @Query(value = "select l " +
            "from Learning l " +
            "where l.id IN :learningIdList")
    List<Learning> findLearningListByLearningIdList(List<Long> learningIdList);
}
