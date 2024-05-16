package com.choidh.service.notification.vo;


import com.choidh.service.learning.entity.Learning;
import lombok.Getter;

@Getter
public class LearningClosedEvent {

    private Learning learning;

    public LearningClosedEvent(Learning learning) {
        this.learning = learning;
    }
}
