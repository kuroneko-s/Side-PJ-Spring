package com.choidh.service.joinTables.repository;

import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LearningCartRepository extends JpaRepository<LearningCartJoinTable, Long> {
    // @Query(value = "delete from LearningCartJoinTable where cart.id = :cartId and learning.id = :learningId")
    int deleteByCartIdAndLearningId(Long cartId, Long learningId);

    @Query(value = "select lcjt from LearningCartJoinTable lcjt join fetch lcjt.learning where lcjt.cart.id = :cartId")
    List<LearningCartJoinTable> findAllWithLearningByCartId(Long cartId);
}
