package com.choidh.service.learning.vo.web;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModLearningVO {
    private String title;

    private String simpleSubscription;

    private String subscription;

    private int price;

    private String mainCategory;

    private String skills;
}
