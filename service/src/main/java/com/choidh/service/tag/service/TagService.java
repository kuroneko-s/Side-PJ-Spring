package com.choidh.service.tag.service;

import java.util.List;

public interface TagService {
    // 태그 타이틀 목록 조회
    List<String> getTitleList();

    // 태그 추가 For 강의
    void addTagsForLearning(String tagTitle, Long learningId);

    // 태그 삭제 For 강의
    int removeTagsForLearning(String tagTitle, Long learningId);
}
