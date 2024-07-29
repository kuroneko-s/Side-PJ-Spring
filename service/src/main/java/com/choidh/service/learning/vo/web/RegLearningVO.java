package com.choidh.service.learning.vo.web;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegLearningVO {
    private String title;

    private int price;

    private String subscription;

    private String simpleSubscription;

    private String mainCategory;

    private String skills;
}
