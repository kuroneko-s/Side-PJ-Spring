package com.choidh.service.review.vo;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegReviewVO {
    private String description;

    private float rating;
}
