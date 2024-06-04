package com.choidh.service.joinTables.service;

import com.choidh.service.tag.entity.Tag;

import java.util.List;

public interface LearningTagService {
    List<Tag> findListByLearningId(Long learningId);
}
