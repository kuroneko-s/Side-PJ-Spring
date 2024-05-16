package com.choidh.web.question.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class QuestionForm {
    private String s_title;
    private String s_description;
    private String s_answer;
}
