package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;

import java.util.List;
import java.util.Set;

public interface LearningTagService {
    /**
     * Learning Tag 목록 조회 By Learning Id
     */
    List<Tag> getListByLearningId(Long learningId);

    /**
     * Learning Tag 목록 조회 By Tags Ids
     */
    Set<LearningTagJoinTable> getListByTags(Set<Tag> tags);

    /**
     * Learning Tag 조인 테이블 생성
     */
    LearningTagJoinTable regLearningTagJointable(Learning learning, Tag tag);

    /**
     * Learning Tag 조인 테이블 삭제 By Id
     */
    void delLearningTagJoinTable(LearningTagJoinTable learningTagJoinTable);
}
