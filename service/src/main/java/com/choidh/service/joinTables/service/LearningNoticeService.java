package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningNoticeJoinTable;

public interface LearningNoticeService {
    /**
     * LearningNoticeJoinTable 등록
     */
    LearningNoticeJoinTable regLearningNoticeJoinTable(Long learningId, Long noticeId);
}
