package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.repository.LearningCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LearningCartServiceImpl implements LearningCartService {
    private final LearningCartRepository learningCartRepository;

    /**
     * LearningCartJoinTable 목록 조회. By Cart Id
     */
    @Override
    public Set<LearningCartJoinTable> getCartListWithLearningByCartId(Long cartId) {
        return learningCartRepository.findListWithLearningByCartId(cartId);
    }

    @Override
    public void saveLearningCart(LearningCartJoinTable learningCartJoinTable) {
        learningCartRepository.save(learningCartJoinTable);
    }

    @Override
    public int removeLearning(Long cartId, Long learningId) {
        return learningCartRepository.deleteByCartIdAndLearningId(cartId, learningId);
    }
}
