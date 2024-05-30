package com.choidh.service.joinTables.repository;

import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LearningTagRepository extends JpaRepository<LearningTagJoinTable, Long> {
    @Query(value = "delete from LearningTagJoinTable ltjt where ltjt.learning.id = :learningId and ltjt.tag.title = :tagTitle")
    int deleteByLearningIdAndTagTitle(Long learningId, String tagTitle);

    @Query(value = "select ltjt.tag " +
            "from LearningTagJoinTable ltjt " +
            "where ltjt.learning.id = :learningId")
    List<Tag> findAllByLearningId(Long learningId);
}
