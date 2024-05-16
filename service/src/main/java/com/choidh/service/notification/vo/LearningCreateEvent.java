package com.choidh.service.notification.vo;


import com.choidh.service.learning.entity.Learning;
import lombok.Getter;

@Getter
public class LearningCreateEvent {

    private Learning learning;

    public LearningCreateEvent(Learning learning) {
        this.learning = learning;
    }
}
