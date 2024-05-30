package com.choidh.service.question.service;


import com.choidh.service.question.entity.Question;
import com.choidh.service.question.vo.ModQuestionVO;
import com.choidh.service.question.vo.RegQuestionVO;

public interface QuestionService {
    /**
     * GET 질의 글 By ID
     */
    Question getQuestionById(Long questionId);

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
