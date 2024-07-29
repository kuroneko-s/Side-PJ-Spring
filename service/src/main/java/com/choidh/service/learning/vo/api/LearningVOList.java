package com.choidh.service.learning.vo.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class LearningVOList {
    private List<LearningResponse> learningResponseList;

    public LearningVOList(List<LearningResponse> learningResponseList) {
        this.learningResponseList = new ArrayList<>(learningResponseList);
    }
}
