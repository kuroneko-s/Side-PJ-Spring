package com.choidh.service.joinTables.repository;

import com.choidh.service.joinTables.entity.LearningNoticeJoinTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningNoticeRepository extends JpaRepository<LearningNoticeJoinTable, Long> {
}
