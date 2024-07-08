package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;

import java.util.List;

public interface LearningTagService {
    List<Tag> findListByLearningId(Long learningId);

    /**
     * Learning Tag 조인 테이블 생성
     */
    LearningTagJoinTable regLearningTagJointable(Learning learning, Tag tag);

    /**
     * Learning Tag 조인 테이블 삭제 By Id
     */
    void delLearningTagJoinTable(LearningTagJoinTable learningTagJoinTable);
}
