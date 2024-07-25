package com.choidh.app.learnieng.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class LearningVOList {
    private List<LearningVO> learningVOList;

    public LearningVOList(List<LearningVO> learningVOList) {
        this.learningVOList = new ArrayList<>(learningVOList);
    }
}
