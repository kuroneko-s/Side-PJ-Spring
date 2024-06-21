package com.choidh.web.learning.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.service.LearningCartService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.learning.vo.LearningDetailVO;
import com.choidh.service.learning.vo.LearningListAPIVO;
import com.choidh.service.learning.vo.LearningListVO;
import com.choidh.service.learning.vo.LearningListenVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.kakao.vo.KakaoPayForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.getTitle;

@Controller
@RequestMapping(value = "")
@RequiredArgsConstructor
public class LearningController {
    private final LearningService learningService;
    private final AttachmentService attachmentService;
    private final CartService cartService;
    private final LearningCartService learningCartService;
    private final AccountService accountService;

    /**
     * Get Learning 목록 View. By keyword Learning
     */
    @GetMapping("/learning/search/{keyword}")
    public String getLearningListByKeywordView(@CurrentAccount Account account, Model model, @PathVariable("keyword") String mainCategory,
                                               @RequestParam(name = "keyword", defaultValue = "", required = false) String subCategory,
                                               @PageableDefault(size = 16, sort = "openingDate", direction = Sort.Direction.DESC) Pageable pageable){
        LearningListVO learningListVO = learningService.getLearningListByViewWithKeyword(mainCategory, subCategory, pageable);

        model.addAttribute("mainCategory", mainCategory);
        model.addAttribute("subCategory", subCategory);
        model.addAttribute("learningList", learningListVO.getLearningPage().getContent());
        model.addAttribute("learningImageMap", learningListVO.getLearningImageMap());
        model.addAttribute("pageTitle", getTitle("검색"));
        model.addAttribute(learningListVO.getPaging());

        return "learning/list/index";
    }

    /**
     * Post Learning 페이징 API. By keyword Learning
     */
    @PostMapping("/learning/search/{keyword}")
    @ResponseBody
    public ResponseEntity postLearningListByKeyword(@PathVariable("keyword") String mainCategory,
                                                    @RequestBody(required = false) String keyword,
                                                    @PageableDefault(size = 16, sort = "openingDate", direction = Sort.Direction.DESC) Pageable pageable) throws JsonProcessingException {
        LearningListAPIVO learningListAPIVO = learningService.getLearningListByViewWithKeywordOfAPI(mainCategory, keyword, pageable);

        return ResponseEntity.ok().body(learningListAPIVO);
    }

    /**
     * Get Learning 상세 View.
     */
    @GetMapping("/learning/{learningId}")
    public String getLearningDetailView(@CurrentAccount Account account, Model model, @PathVariable Long learningId) {
        LearningDetailVO learningDetail = learningService.getLearningDetail(account != null ? account.getId() : -1, learningId);
        Learning learning = learningDetail.getLearning();

        model.addAttribute("learning", learning);
        model.addAttribute("professionalAccount", learningDetail.getProfessionalAccount());
        model.addAttribute("questions", learningDetail.getQuestion());
        model.addAttribute("reviews", learningDetail.getReviews());
        model.addAttribute("bannerImage", learningDetail.getBannerImage());
        model.addAttribute("videoFileList", learningDetail.getVideoFilesList());
        model.addAttribute("nowListening", learningDetail.isNowListening());

        model.addAttribute("pageTitle", getTitle(learning.getTitle()));

        return "learning/detail/index";
    }

    /**
     * 강의 학습 페이지로 이동. (학습 버튼 클릭시 동작)
     */
    @GetMapping("/learning/listen/{learningId}")
    public String getLearningListenView(@CurrentAccount Account account, Model model, @PathVariable Long learningId) {
        LearningListenVO learningListen = learningService.getLearningListen(account.getId(), learningId);
        Learning learning = learningListen.getLearning();

        model.addAttribute("learning", learning);
        model.addAttribute("titleSet", learningListen.getVideoTitleSet());
        model.addAttribute("videoSrc", learningListen.getVideoSrc());
        model.addAttribute("videoFileIdMap", learningListen.getVideoFileIdMap());
        model.addAttribute("playingVideo", learningListen.getPlayingVideo());

        model.addAttribute("pageTitle", getTitle(learning.getTitle()));

        return "learning/listen/index";
    }

    /**
     * 강의 학습 페이지. 영상 경로 조회. API
     */
    @PostMapping("/learning/listen/{learningId}")
    @ResponseBody
    public ResponseEntity getPlayLearningVideoView(@CurrentAccount Account account, @PathVariable("learningId") Long learningId,
                                                   @RequestBody String fileId) {
        if (account == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            accountService.chkAccountHasLearning(account.getId(), learningId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        AttachmentFile attachmentFile = attachmentService.getAttachmentFileById(Long.parseLong(fileId));
        return ResponseEntity.ok(attachmentFile.getFullPath(""));
    }
}
