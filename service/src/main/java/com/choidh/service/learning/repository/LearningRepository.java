package com.choidh.service.learning.repository;


// LOAD 선택 - EAGER 그외 - 기본전략, LAZY 선택 - EAGER 그외 - LAZY

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.sun.istack.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface LearningRepository extends JpaRepository<Learning, Long>, QuerydslPredicateExecutor<Learning>, LearningRepositoryExtension {
    Learning findByTitle(String title);

    @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Learning> findTop4ByStartingLearningOrderByCreateLearningDesc(boolean b);

    @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Learning> findTop4ByStartingLearningOrderByRatingDesc(boolean b);

    @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Learning> findTop12ByStartingLearningOrderByRatingDesc(boolean b);

    List<Learning> findAllByAccountOrderByCreateLearningDesc(Account account);

    Learning findByIdAndLecturerName(Long id, @NotNull String lecturerName);

    @EntityGraph(attributePaths = {"tags", "reviews", "questions", "videos"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Learning> findLearningWithVideosAndTagsAndReviewsAndQuestionsById(Long id);

    @EntityGraph(attributePaths = {"videos"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Learning> findLearningWithVideosById(Long id);

    @EntityGraph(attributePaths = {"questions", "tags"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Learning> findLearningWithQuestionsAndTagsById(Long id);
}
