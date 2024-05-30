package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningCartJoinTable;

import java.util.List;

public interface LearningCartService {
    List<LearningCartJoinTable> getCartListWithLearningByCartId(Long cartId);

    void saveLearningCart(LearningCartJoinTable learningCartJoinTable);

    int removeLearning(Long cartId, Long learningId);
}
