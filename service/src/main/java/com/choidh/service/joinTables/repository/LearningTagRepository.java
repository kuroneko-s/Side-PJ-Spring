package com.choidh.service.joinTables.repository;

import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface LearningTagRepository extends JpaRepository<LearningTagJoinTable, Long> {
    /**
     * LearningTagJoinTable 목록 조회. By Learning Id
     */
    @Query(value = "select ltjt " +
            "from LearningTagJoinTable ltjt " +
            "where ltjt.learning.id = :learningId")
    @EntityGraph(attributePaths = {"learning", "tag"}, type = EntityGraph.EntityGraphType.LOAD)
    Set<LearningTagJoinTable> findListByLearningId(Long learningId);

    /**
     * LearningTagJoinTable 삭제. By LearningTagJoinTable Id
     */
    @Query(value = "delete from LearningTagJoinTable ltjt " +
            "where ltjt.id = :learningTagJoinTableId ")
    @Modifying
    int deleteByLearningIdAndTagTitle(Long learningTagJoinTableId);
}
