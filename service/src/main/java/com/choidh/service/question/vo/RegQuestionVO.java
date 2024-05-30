package com.choidh.service.question.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegQuestionVO {
    private String title;
    private String description;
    private String answer;
}
