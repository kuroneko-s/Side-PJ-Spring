package com.choidh.service.question.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.common.pagination.Paging;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.question.entity.Question;
import com.choidh.service.question.repository.QuestionRepository;
import com.choidh.service.question.vo.ModQuestionVO;
import com.choidh.service.question.vo.QuestionListVO;
import com.choidh.service.question.vo.RegQuestionVO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * GET 질의 글 목록 조회.
     */
    @Override
    public QuestionListVO getQuestionList(Pageable pageable, Long accountId, Long learningId) {
        accountService.chkAccountHasLearning(accountId, learningId);

        Page<Question> questionPage = questionRepository.findListByLearningId(pageable, learningId);

        String defaultButtonUrlBuilder = "/question/list/" + learningId + "?sort=questionTime,desc&page=";

        Paging paging = Paging.builder()
                .hasNext(questionPage.hasNext())
                .hasPrevious(questionPage.hasPrevious())
                .number(questionPage.getNumber())
                .paginationUrl(defaultButtonUrlBuilder)
                .totalPages(questionPage.getTotalPages())
                .build();

        return QuestionListVO.builder()
                .questionPage(questionPage)
                .paging(paging)
                .build();
    }

    /**
     * Reg 질의 글 등록
     */
    @Override
    @Transactional
    public void regQuestion(RegQuestionVO regQuestionVO, Long accountId, Long learningId) {
        accountService.chkAccountHasLearning(accountId, learningId);

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
