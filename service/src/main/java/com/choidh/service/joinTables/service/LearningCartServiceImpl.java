package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.repository.LearningCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningCartServiceImpl implements LearningCartService {
    private final LearningCartRepository learningCartRepository;

    @Override
    public List<LearningCartJoinTable> getCartListWithLearningByCartId(Long cartId) {
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
