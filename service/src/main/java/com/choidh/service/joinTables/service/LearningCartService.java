package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningCartJoinTable;

import java.util.Set;

public interface LearningCartService {
    /**
     * LearningCartJoinTable 목록 조회. By Cart Id
     */
    Set<LearningCartJoinTable> getCartListWithLearningByCartId(Long cartId);

    void saveLearningCart(LearningCartJoinTable learningCartJoinTable);

    int removeLearning(Long cartId, Long learningId);
}
