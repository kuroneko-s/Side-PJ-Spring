package com.choidh.web.review.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.CodeConstant;
import com.choidh.service.common.StringUtils;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.review.service.ReviewService;
import com.choidh.service.review.vo.RegReviewVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.review.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.choidh.service.common.AppConstant.getTitle;

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
    @GetMapping("/regReview")
    public String getRegReviewView(@CurrentAccount Account account, Model model, @RequestParam("learningId") Long learningId){
        Learning learning = learningService.getLearningById(learningId);

        model.addAttribute("learning", learning);
        model.addAttribute(new ReviewVO());

        model.addAttribute("pageTitle", getTitle("리뷰 작성"));

        return "review/create/index";
    }

    /**
     * 리뷰 추가. API.
     */
    @PostMapping("/{learningId}")
    public ResponseEntity saveReview(@CurrentAccount Account account, @PathVariable Long learningId, @RequestBody ReviewVO reviewVO) {
        // 리뷰를 작성했던 적이 있으면 올릴 수 없음.
        boolean extReview = reviewService.extReview(account.getId(), learningId);
        if (extReview) {
            return ResponseEntity.ok(CodeConstant.DUPLICATED);
        }

        // 리뷰 추가.
        reviewService.regReview(modelMapper.map(reviewVO, RegReviewVO.class), account.getId(), learningId);

        return ResponseEntity.ok(CodeConstant.SUCCESS);
    }
}
