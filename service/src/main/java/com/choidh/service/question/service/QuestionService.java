package com.choidh.service.question.service;


import com.choidh.service.question.entity.Question;
import com.choidh.service.question.vo.ModQuestionVO;
import com.choidh.service.question.vo.QuestionListVO;
import com.choidh.service.question.vo.RegQuestionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    /**
     * GET 질의 글 조회. By ID
     */
    Question getQuestionById(Long questionId);

    /**
     * GET 질의 글 목록 조회.
     */
    QuestionListVO getQuestionList(Pageable pageable, Long accountId, Long learningId);

    /**
     * Reg 질의 글 등록
     */
    void regQuestion(RegQuestionVO regQuestionVO, Long accountId, Long learningId);

    /**
     * Mod 질의 글 수정
     */
    Question modQuestion(ModQuestionVO modQuestionVO);

    void delQuestion(Long questionId);

    boolean chkQuestionOwner(Long accountId, Long questionId);
}
