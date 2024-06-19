package com.choidh.web.question.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.joinTables.service.LearningTagService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.question.entity.Question;
import com.choidh.service.question.service.QuestionService;
import com.choidh.service.question.vo.ModQuestionVO;
import com.choidh.service.question.vo.RegQuestionVO;
import com.choidh.service.tag.entity.Tag;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.question.vo.QuestionForm;
import com.choidh.web.question.vo.RegQuestionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.getTitle;

@Controller
@RequestMapping(value = "/question")
@RequiredArgsConstructor
public class QuestionController {
    private final LearningService learningService;
    private final ModelMapper modelMapper;
    private final QuestionService questionService;
    private final AccountService accountService;
    private final LearningTagService learningTagService;
    private final AttachmentService attachmentService;

    /**
     * Get 질문 글 목록 View.
     */
    @GetMapping("/list")
    public String getQuestionListView(@CurrentAccount Account account, Model model) {
        return "";
    }

    /**
     * Get 질문 글 등록 View.
     */
    @GetMapping("/{learningId}")
    public String getRegQuestionView(@CurrentAccount Account account, Model model, @PathVariable Long learningId){
        // 해당 강의에 질의 글 등록 권한이 있는지 확인.
        accountService.chkAccountHasLearning(account.getId(), learningId);

        Learning learning = learningService.getLearningById(learningId);

        model.addAttribute("learning", learning);
        model.addAttribute("account", account);
        model.addAttribute("regQuestionForm", new RegQuestionForm());

        model.addAttribute("pageTitle", getTitle("질문 등록"));

        return "question/create/index";
    }

    // 질의 글 등록
    @PostMapping("/{learningId}")
    public ResponseEntity regQuestion(@CurrentAccount Account account, @PathVariable Long learningId, @RequestBody RegQuestionForm regQuestionForm){
        questionService.regQuestion(modelMapper.map(regQuestionForm, RegQuestionVO.class), account.getId(), learningId);

        return ResponseEntity.ok().build();
    }

    /**
     * Get 질문 글 상세 View.
     */
    @GetMapping("/{learningId}/{questionId}")
    public String getQuestionDetailView(@CurrentAccount Account account, Model model,
                                        @PathVariable Long learningId, @PathVariable Long questionId) {
        return "";
    }

    // 질문 화면 이동
    @GetMapping("/learning/question/{learningId}")
    public String getLearningQuestionView(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long id){
        Learning learning = learningService.getLearningByIdWithQuestion(id);
        List<Tag> tagList = learningTagService.findListByLearningId(id);
        int videoCount = attachmentService.cntAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);

        model.addAttribute("account", account);
        model.addAttribute("questionForm", new QuestionForm());
        model.addAttribute("countVideo", videoCount);
        model.addAttribute("learning", learning);
        model.addAttribute("tags", tagList.stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("ratings", learning.getRatingInt());
        model.addAttribute("halfrating", learning.checkRatingBoolean());
        model.addAttribute("rating", learning.emptyRating());
        model.addAttribute("learningRating", learning.getRating());
        model.addAttribute("questions", learning.getQuestions());
        model.addAttribute("idList", learning.getQuestions().stream().map(Question::getId).collect(Collectors.toList()));

        return "learning/question";
    }

    // 질의글에 답변 수정
    @PostMapping("/learning/question/{questionId}/update")
    public String updateQuestionAnswer(@CurrentAccount Account account, RedirectAttributes attributes, QuestionForm questionForm) {
        Question question = questionService.modQuestion(modelMapper.map(questionForm, ModQuestionVO.class));

        attributes.addFlashAttribute("account", account);
        Learning learning = question.getLearning();

        return "redirect:/learning/question/" + learning.getId();
    }

    // 질의글에 새로운 답변 등록
    @PostMapping("/learning/question/{questionId}/create")
    public String createQuestionAnswer(@CurrentAccount Account account, RedirectAttributes attributes, QuestionForm questionForm) {
        Question question = questionService.modQuestion(modelMapper.map(questionForm, ModQuestionVO.class));

        attributes.addFlashAttribute("account", account);
        Learning learning = question.getLearning();

        return "redirect:/learning/question/" + learning.getId();
    }

    // 질의글에 답변 삭제
    @GetMapping("/learning/question/{questionId}/delete")
    public String deleteQuestionAnswer(@CurrentAccount Account account, RedirectAttributes attributes,
                                       @PathVariable("questionId") Long id) {
        if (questionService.chkQuestionOwner(account.getId(), id)) {
            attributes.addFlashAttribute("message", "잘못된 요청입니다");

            // 왜 계속 강의쪽 URL로 보내는거지 ??????
            return "redirect:/learning/question/" + id;
        }

        questionService.delQuestion(id);

        attributes.addFlashAttribute("account", account);

        return "redirect:/learning/question/" + id;
    }
}

