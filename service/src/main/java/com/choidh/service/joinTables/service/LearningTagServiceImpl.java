package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.repository.LearningTagRepository;
import com.choidh.service.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningTagServiceImpl implements LearningTagService {
    private final LearningTagRepository learningTagRepository;

    @Override
    public List<Tag> findAllByLearningId(Long learningId) {
        return learningTagRepository.findAllByLearningId(learningId);
    }
}
