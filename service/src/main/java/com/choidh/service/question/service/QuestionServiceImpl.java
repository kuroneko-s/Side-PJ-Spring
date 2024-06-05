package com.choidh.service.question.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.question.entity.Question;
import com.choidh.service.question.repository.QuestionRepository;
import com.choidh.service.question.vo.ModQuestionVO;
import com.choidh.service.question.vo.RegQuestionVO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final AccountService accountService;
    private final LearningService learningService;
    private final ModelMapper modelMapper;

    /**
     * GET 질의 글 By ID
     */
    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(questionId + "에 해당하는 질의글을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void regQuestion(RegQuestionVO regQuestionVO, Long accountId, Long learningId) {
        Account account = accountService.getAccountById(accountId);
        Learning learning = learningService.getLearningById(learningId);

        Question question = modelMapper.map(regQuestionVO, Question.class);
        question.setQuestionTime(LocalDateTime.now());
        question.setAccount(account);
        question.setLearning(learning);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question modQuestion(ModQuestionVO modQuestionVO) {
        Question question = this.getQuestionById(modQuestionVO.getQuestionId());
        question.setAnswer(modQuestionVO.getAnswer());
        question.setDescription(modQuestionVO.getDescription());

        return question;
    }

    @Override
    public void delQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    @Override
    public boolean chkQuestionOwner(Long accountId, Long questionId) {
        Account account = accountService.getAccountByIdWithLearningInCart(accountId);
        Question question = this.getQuestionById(questionId);

        return account.getId().equals(question.getAccount().getId());
    }
}
