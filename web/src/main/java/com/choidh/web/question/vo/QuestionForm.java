package com.choidh.web.question.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class QuestionForm {
    private Long questionId;
    private String title;
    private String description;
    private String answer;
}
