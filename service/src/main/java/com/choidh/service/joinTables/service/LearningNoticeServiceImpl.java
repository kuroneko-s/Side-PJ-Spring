package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.LearningNoticeJoinTable;
import com.choidh.service.joinTables.repository.LearningNoticeRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.notice.entity.Notice;
import com.choidh.service.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LearningNoticeServiceImpl implements LearningNoticeService {
    @Autowired
    private LearningService learningService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private LearningNoticeRepository learningNoticeRepository;

    /**
     * LearningNoticeJoinTable 등록
     */
    @Override
    public LearningNoticeJoinTable regLearningNoticeJoinTable(Long learningId, Long noticeId) {
        Learning learning = learningService.getLearningById(learningId);
        Notice notice = noticeService.getNoticeById(noticeId);

        LearningNoticeJoinTable learningNoticeJoinTable = learningNoticeRepository.save(LearningNoticeJoinTable.builder()
                .learning(learning)
                .notice(notice)
                .build());
        learning.getNotices().add(learningNoticeJoinTable);

        return learningNoticeJoinTable;
    }
}
