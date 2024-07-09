package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.joinTables.repository.LearningTagRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LearningTagServiceImpl implements LearningTagService {
    private final LearningTagRepository learningTagRepository;

    @Override
    public List<Tag> getListByLearningId(Long learningId) {
        Set<LearningTagJoinTable> LearningTagJoinTableList = learningTagRepository.findListByLearningId(learningId);
        return LearningTagJoinTableList.stream().map(learningTagJoinTable -> learningTagJoinTable.getTag()).collect(Collectors.toList());
    }

    /**
     * Learning Tag 목록 조회 By Tags Ids
     */
    @Override
    public Set<LearningTagJoinTable> getListByTags(Set<Tag> tags) {
        return learningTagRepository.findListByTags(tags);
    }

    /**
     * Learning Tag 조인 테이블 생성
     */
    @Override
    public LearningTagJoinTable regLearningTagJointable(Learning learning, Tag tag) {
        LearningTagJoinTable learningTagJoinTable = learningTagRepository.findByLearningIdAndTagId(learning.getId(), tag.getId());

        if (learningTagJoinTable == null) {
            learningTagJoinTable = learningTagRepository.save(LearningTagJoinTable.builder()
                    .learning(learning)
                    .tag(tag)
                    .build());
        }

        return learningTagJoinTable;
    }

    /**
     * Learning Tag 조인 테이블 삭제 By Id
     */
    @Override
    public void delLearningTagJoinTable(LearningTagJoinTable learningTagJoinTable) {
        learningTagRepository.deleteByLearningId(learningTagJoinTable.getId());
    }
}
