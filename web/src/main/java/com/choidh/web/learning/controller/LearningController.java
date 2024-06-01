package com.choidh.web.learning.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.service.LearningCartService;
import com.choidh.service.joinTables.service.LearningTagService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.question.entity.Question;
import com.choidh.service.question.service.QuestionService;
import com.choidh.service.question.vo.ModQuestionVO;
import com.choidh.service.question.vo.RegQuestionVO;
import com.choidh.service.review.service.ReviewService;
import com.choidh.service.review.vo.RegReviewVO;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.service.TagService;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.kakao.vo.KakaoPayForm;
import com.choidh.web.learning.validator.LearningValidator;
import com.choidh.web.question.vo.QuestionForm;
import com.choidh.web.review.vo.ReviewVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.getQuestionNotFoundErrorMessage;

@Controller
@RequiredArgsConstructor
public class LearningController {
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final LearningService learningService;
    private final LearningValidator learningValidator;
    private final AttachmentService attachmentService;
    private final AccountService accountService;
    private final QuestionService questionService;
    private final ReviewService reviewService;
    private final TagService tagService;
    private final CartService cartService;
    private final LearningCartService learningCartService;
    private final LearningTagService learningTagService;

    @Value("${download.path}")
    private String downloadPath;

    // 강의 상세 페이지로 이동. (학습 버튼 클릭시 동작)
    @GetMapping("/learning/{id}/listen")
    public String getLearningView(@CurrentAccount Account account, Model model, @PathVariable Long id) {
        Learning learning = learningService.getLearningById(id);
        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);
        List<String> contentsTitle = attachmentFiles.stream().map(AttachmentFile::getOriginalFileName).sorted().collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("contentsList", attachmentFiles.stream().map(AttachmentFile::getOriginalFileName).sorted().collect(Collectors.toList()));
        model.addAttribute("now", contentsTitle.get(0));

        return "learning/listen_learning";
    }

    // 영상 재생하기인가 ?
    @GetMapping("/learning/listen/{learningId}/{fileId}")
    public String getPlayLearningVideoView(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long learningId,
                                           @PathVariable("fileId") Long fileId, RedirectAttributes attributes) {
        Learning learning = learningService.getLearningById(learningId);
        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);
        List<String> contentsTitle = attachmentFiles.stream().map(AttachmentFile::getOriginalFileName).sorted().collect(Collectors.toList());
        AttachmentFile attachmentFile = attachmentService.getAttachmentFileById(learning.getAttachmentGroup().getId(), fileId);

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("contentsList", contentsTitle);
        model.addAttribute("now", attachmentFile.getOriginalFileName());
        model.addAttribute("videoPath", attachmentFile.getFullPath(downloadPath));

        return "learning/listen_learning";
    }

    // 강의 페이지에서 질의하기 팝업 보여주는거 같은데...
    @GetMapping("/question/{id}")
    public String getQuestionView(@CurrentAccount Account account, Model model, @PathVariable Long id){
        Learning learning = learningService.getLearningById(id);

        model.addAttribute("learning", learning);
        model.addAttribute("account", account);
        model.addAttribute(new QuestionForm());

        return "question";
    }

    // 질의 글 등록
    @PostMapping("/question/{id}")
    public String saveQuestionPopup(@CurrentAccount Account account, RedirectAttributes attributes, @PathVariable Long id, QuestionForm questionForm){
        questionService.regQuestion(modelMapper.map(questionForm, RegQuestionVO.class), account.getId(), id);

        attributes.addFlashAttribute("account", account);

        return "redirect:/learning/" + id;
    }

    /**
     * 리뷰 페이지
     */
    @GetMapping("/review/{id}")
    public String showReviewPopup(@CurrentAccount Account account, Model model, @PathVariable Long id){
        Learning learning = learningService.getLearningById(id);

        model.addAttribute("learning", learning);
        model.addAttribute("account", account);
        model.addAttribute(new ReviewVO());

        return "review";
    }

    /**
     * 리뷰 추가.
     */
    @PostMapping("/review/{id}")
    public String saveReview(@CurrentAccount Account account, @PathVariable Long id, Model model,
                             @Valid ReviewVO reviewVO, Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("message", "잘못입력하셨습니다!");

            return "review";
        }

        // 리뷰 추가.
        Account newAccount = reviewService.regReview(modelMapper.map(reviewVO, RegReviewVO.class), account.getId(), id);

        attributes.addFlashAttribute("account", newAccount);

        return "redirect:/learning/" + id;
    }

    // 강의 페이지에서 구매하기 버튼 누르면 강의 구매 화면으로 전환되는 그 동작.
    @GetMapping("/learning/{learningId}/buy")
    public String getLearningBuyView(@CurrentAccount Account account, RedirectAttributes attributes, @PathVariable("learningId") Long learningId) {
        // 카트에 강의 등록
        cartService.addCart(account.getId(), learningId);

        return "redirect:/learning/buy";
    }

    // 구매화면 이동 같음.
    @GetMapping("/learning/buy")
    public String getBuyView(@CurrentAccount Account account, Model model) {
        List<LearningCartJoinTable> learningCartJoinTableList = learningCartService.getCartListWithLearningByCartId(account.getCart().getId());
        List<Learning> learningList = learningCartJoinTableList.stream().map(LearningCartJoinTable::getLearning).collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("learningList", learningList);
        model.addAttribute("totalPrice", learningList.stream().mapToInt(Learning::getPrice).sum());
        model.addAttribute(new KakaoPayForm());

        return "shop/buy";
    }

    // 질문 화면 이동
    @GetMapping("/learning/question/{learningId}")
    public String getLearningQuestionView(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long id){
        Learning learning = learningService.getLearningByIdWithQuestion(id);
        List<Tag> tagList = learningTagService.findAllByLearningId(id);
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
