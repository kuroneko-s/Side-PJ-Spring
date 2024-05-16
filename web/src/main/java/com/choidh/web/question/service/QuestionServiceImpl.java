package com.choidh.web.question.service;


import com.choidh.service.question.entity.Question;
import com.choidh.service.question.repository.QuestionRepository;
import com.choidh.web.question.vo.QuestionForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Question updateQuestion(QuestionForm questionForm, Long id) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.setS_answer(questionForm.getS_answer());

        return question;
    }

    @Override
    public void deleteQuestion(Question question) {
        questionRepository.deleteById(question.getId());
    }
}
