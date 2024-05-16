package com.choidh.web.review.vo;


import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVO {
    private String description;

    private float rating;
}
