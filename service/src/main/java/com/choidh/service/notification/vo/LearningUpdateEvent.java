package com.choidh.service.notification.vo;


import com.choidh.service.learning.entity.Learning;
import lombok.Getter;

@Getter
public class LearningUpdateEvent {
    private Learning learning;

    public LearningUpdateEvent(Learning learning) {
        this.learning = learning;
    }
}
