package com.choidh.web.professional.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.utiles.StringUtils;
import com.choidh.service.common.vo.CodeConstant;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.learning.vo.web.LearningListVO;
import com.choidh.service.learning.vo.web.LearningModifyVO;
import com.choidh.service.learning.vo.web.ModLearningVO;
import com.choidh.service.learning.vo.web.RegLearningVO;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.service.TagService;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.professional.validator.LearningValidator;
import com.choidh.web.professional.vo.LearningFormVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping("/professional")
@RequiredArgsConstructor
public class ProfessionalController {
    private final LearningService learningService;
    private final ModelMapper modelMapper;
    private final LearningValidator learningValidator;
    private final TagService tagService;

    @InitBinder("learningFormVO")
    private void initVideoForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(learningValidator);
    }

    /**
     * Get 강의 제공자 대시보드 View
     */
    @GetMapping("/dashboard")
    public String getProfessionalDashboardView(@CurrentAccount Account account, Model model) {
        model.addAttribute("pageTitle", getTitle(account.getNickname() + "님의 대시보드"));
        model.addAttribute("pageContent", "professional/contents");

        return "professional/index";
    }

    /**
     * Get 강의 목록 View
     */
    @GetMapping("/learning/list")
    public String getLearningListView(@CurrentAccount Account account, Model model,
                                      @PageableDefault(sort = "createdAt", size = 8, direction = Sort.Direction.DESC) Pageable pageable) {
        LearningListVO learningListVO = learningService.getLearningListByProfessionalAccount(account.getId(), pageable);

        model.addAttribute("learningList", learningListVO.getLearningList());
        model.addAttribute("learningImageMap", learningListVO.getLearningImageMap());
        model.addAttribute("imageUploadMap", learningListVO.getImageUploadMap());
        model.addAttribute(learningListVO.getPaging());

        model.addAttribute("pageTitle", getTitle(account.getNickname() + "님의 강의 목록"));
        model.addAttribute("pageContent", "professional/learning/list/contents");

        return "professional/index";
    }

    /**
     * Get 강의 등록 View
     */
    @GetMapping("/learning/create")
    public String getRegLearningView(Model model) {
        String whiteList = StringUtils.translationListToString(tagService.getTitleList());

        model.addAttribute("whiteList", whiteList);
        model.addAttribute("pageTitle", getTitle("강의 등록"));
        model.addAttribute("pageContent", "professional/learning/create/contents");

        return "professional/index";
    }

    /**
     * Post 강의 등록
     */
    @PostMapping("/learning/create")
    public String postLearning(@CurrentAccount Account account, Model model,
                               @Valid LearningFormVO learningFormVO, Errors errors) {
        String whiteList = StringUtils.translationListToString(tagService.getTitleList());

        model.addAttribute("whiteList", whiteList);

        if (errors.hasErrors()) {
            model.addAttribute("message", "잘못 입력 하셨습니다.");
            model.addAttribute("pageContent", "professional/learning/create/contents");

            return "professional/index";
        }

        Learning learning = learningService.regLearning(modelMapper.map(learningFormVO, RegLearningVO.class), account.getId());

        return "redirect:/professional/learning/video/" + learning.getId();
    }

    /**
     * Get 강의 내용 수정 View
     */
    @GetMapping("/learning/{learningId}")
    public String getLearningContextModifyView(@PathVariable Long learningId, Model model) {
        Learning learning = learningService.getLearningWithTagsById(learningId);
        String whiteList = StringUtils.translationListToString(tagService.getTitleList());

        List<String> tagList = learning.getTags().stream().map(LearningTagJoinTable::getTag).map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("learning", learning);
        model.addAttribute("whiteList", whiteList);
        model.addAttribute("tagList", String.join(",", tagList));

        model.addAttribute("pageTitle", getTitle(learning.getTitle() + "강의 내용 수정"));
        model.addAttribute("pageContent", "professional/learning/modify/contents");

        return "professional/index";
    }

    /**
     * Post 강의 내용 수정
     */
    @PostMapping("/learning/{learningId}")
    public String modLearningContext(@CurrentAccount Account account, @PathVariable Long learningId, Model model,
                                     @Valid LearningFormVO learningFormVO, Errors errors) {
        // 강의 내용 수정 동작
        if (!errors.hasErrors()) {
            learningService.modLearningContext(
                    modelMapper.map(learningFormVO, ModLearningVO.class),
                    account.getId(), learningId
            );

            return "redirect:/professional/learning/list";
        }

        Learning learning = learningService.getLearningWithTagsById(learningId);
        String whiteList = StringUtils.translationListToString(tagService.getTitleList());
        List<String> tagList = learning.getTags().stream().map(LearningTagJoinTable::getTag).map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute("learning", learning);
        model.addAttribute("whiteList", whiteList);
        model.addAttribute("tagList", String.join(",", tagList));

        model.addAttribute("pageTitle", getTitle(learning.getTitle() + "강의 내용 수정"));
        model.addAttribute("pageContent", "professional/learning/modify/contents");

        return "professional/index";
    }

    /**
     * Get 강의 영상 수정 View
     */
    @GetMapping("/learning/video/{learningId}")
    public String getLearningVideoModifyView(@PathVariable Long learningId, Model model) {
        LearningModifyVO learningModifyVO = learningService.getLearningFilesByLearningId(learningId);
        Learning learning = learningModifyVO.getLearning();

        model.addAttribute("learningModifyVO", learningModifyVO);
        model.addAttribute("learning", learning);

        model.addAttribute("pageTitle", getTitle(learning.getTitle() + "강의 영상 수정"));
        model.addAttribute("pageContent", "professional/learning/upload/contents");

        return "professional/index";
    }

    /**
     * Post 강의 배너 이미지 업로드
     */
    @PostMapping(value = "/learning/banner/upload")
    public ResponseEntity modLearningBanner(@RequestParam("learningId") Long learningId,
                                            @RequestParam(name = "banner") MultipartFile bannerFile) {
        learningService.modLearningBannerImage(learningId, bannerFile);

        return ResponseEntity.ok(CodeConstant.SUCCESS);
    }

    /**
     * Post 강의 영상 업로드
     */
    @PostMapping("/learning/video/upload")
    public ResponseEntity modLearningVideo(@RequestParam("learningId") Long learningId,
                                           @RequestParam("title") String title,
                                           @RequestParam("order") Integer order,
                                           @RequestPart("video") MultipartFile videoFile) {
        learningService.modLearningVideo(learningId, title, order, videoFile);

        return ResponseEntity.ok(CodeConstant.SUCCESS);
    }

    /**
     * Post 강의 영상 공개
     */
    @PostMapping("/learning/opening/{learningId}")
    public ResponseEntity modLearningVideo(@CurrentAccount Account account, @PathVariable("learningId") Long learningId) {
        learningService.modOpeningLearning(account.getId(), learningId);

        return ResponseEntity.ok(CodeConstant.SUCCESS);
    }
}
