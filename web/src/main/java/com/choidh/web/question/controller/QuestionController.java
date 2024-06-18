package com.choidh.web.question.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.question.service.QuestionService;
import com.choidh.service.question.vo.RegQuestionVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.question.vo.QuestionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/question")
@RequiredArgsConstructor
public class QuestionController {
    private final LearningService learningService;
    private final ModelMapper modelMapper;
    private final QuestionService questionService;
    private final AccountService accountService;

    /**
     * Get 질문 글 등록 View.
     */
    @GetMapping("/{learningId}")
    public String regQuestionView(@CurrentAccount Account account, Model model, @PathVariable Long learningId){
        // 해당 강의에 질의 글 등록 권한이 있는지 확인.
        accountService.chkAccountHasLearning(account.getId(), learningId);

        Learning learning = learningService.getLearningById(learningId);

        model.addAttribute("learning", learning);
        model.addAttribute("account", account);
        model.addAttribute(new QuestionForm());

        return "question/create/question";
    }

    // 질의 글 등록
    @PostMapping("/{id}")
    public String saveQuestionPopup(@CurrentAccount Account account, RedirectAttributes attributes, @PathVariable Long id, QuestionForm questionForm){
        questionService.regQuestion(modelMapper.map(questionForm, RegQuestionVO.class), account.getId(), id);

        attributes.addFlashAttribute("account", account);

        return "redirect:/learning/" + id;
    }

}

