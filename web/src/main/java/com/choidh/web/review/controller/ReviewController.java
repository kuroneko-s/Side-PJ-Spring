package com.choidh.web.review.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.review.service.ReviewService;
import com.choidh.service.review.vo.RegReviewVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.review.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/review")
@RequiredArgsConstructor
public class ReviewController {
    private final LearningService learningService;
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;

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
}
