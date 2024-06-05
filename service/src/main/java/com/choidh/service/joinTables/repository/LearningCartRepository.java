package com.choidh.service.joinTables.repository;

import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface LearningCartRepository extends JpaRepository<LearningCartJoinTable, Long> {
    /**
     * LearningCartJoinTable 삭제. By Cart Id And Learning Id
     */
    @Query(value = "delete from LearningCartJoinTable " +
            "where cart.id = :cartId " +
            "and learning.id = :learningId")
    @Modifying
    int deleteByCartIdAndLearningId(Long cartId, Long learningId);

    /**
     * LearningCartJoinTable 목록 조회. By Cart Id
     */
    @Query(value = "select lcjt " +
            "from LearningCartJoinTable lcjt " +
            "join fetch lcjt.learning " +
            "where lcjt.cart.id = :cartId")
    @EntityGraph(attributePaths = {"cart.account.professionalAccount"}, type = EntityGraph.EntityGraphType.LOAD)
    Set<LearningCartJoinTable> findListWithLearningByCartId(Long cartId);
}
