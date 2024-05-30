package com.choidh.service.learning.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModLearningVO {
    private String title;

    private String simplesubscription;

    private String subscription;

    private String lecturerName;

    private String lecturerDescription;

    private int price;

    private String kategorie;
}
