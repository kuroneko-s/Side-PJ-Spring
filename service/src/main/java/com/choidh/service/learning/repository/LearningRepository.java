package com.choidh.service.learning.repository;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.sun.istack.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@EnableJpaRepositories
public interface LearningRepository extends JpaRepository<Learning, Long>, QuerydslPredicateExecutor<Learning>, LearningRepositoryExtension {
    Learning findByTitle(String title);

    @Query(value = "select l from Learning l join fetch l.tags where l.id = :learningId")
    Learning findByIdWithTags(Long learningId);

    @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Learning> findTop4ByOpeningOrderByOpeningDateDesc(boolean b);

    // @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.LOAD)
    // List<Learning> findTop4ByStartingLearningOrderByRatingDesc(boolean b);

    @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Learning> findTop12ByOpeningOrderByRatingDesc(boolean b);

    // List<Learning> findAllByAccountOrderByCreateLearningDesc(Account account);

    // Learning findByIdAndLecturerName(Long id, @NotNull String lecturerName);

//    @EntityGraph(attributePaths = {"tags", "reviews", "questions", "videos"}, type = EntityGraph.EntityGraphType.LOAD)
//    Optional<Learning> findLearningWithVideosAndTagsAndReviewsAndQuestionsById(Long id);
//
//    @EntityGraph(attributePaths = {"videos"}, type = EntityGraph.EntityGraphType.LOAD)
//    Optional<Learning> findLearningWithVideosById(Long id);
//
//    @EntityGraph(attributePaths = {"questions", "tags"}, type = EntityGraph.EntityGraphType.LOAD)
//    Optional<Learning> findLearningWithQuestionsAndTagsById(Long id);

    @Query(value = "select l " +
            "from Learning l " +
            "where l.id = :learningId")
    @EntityGraph(attributePaths = {"noticesList", "learningCartJoinTables", "professionalAccount", "purchaseHistories", "reviews", "questions", "tags"}, type = EntityGraph.EntityGraphType.LOAD)
    Learning findLearningDetailById(Long learningId);

    @Query(value = "select l " +
            "from Learning l " +
            "join fetch l.questions " +
            "where l.id = :learningId")
    Learning findLearningByLearningIdWithQuestion(Long learningId);

    @Query(value = "select l from Learning l where l in :learningIdList")
    List<Learning> findLearningListByLearningIdList(List<Long> learningIdList);
}
