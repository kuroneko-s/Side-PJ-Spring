package com.choidh.web.question.service;


import com.choidh.service.question.entity.Question;
import com.choidh.web.question.vo.QuestionForm;

public interface QuestionService {
    Question updateQuestion(QuestionForm questionForm, Long id);

    void deleteQuestion(Question question);
}
