package com.choidh.web.professional.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.utiles.StringUtils;
import com.choidh.service.common.vo.CodeConstant;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.learning.vo.LearningListVO;
import com.choidh.service.learning.vo.LearningModifyVO;
import com.choidh.service.learning.vo.ModLearningVO;
import com.choidh.service.learning.vo.RegLearningVO;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.service.TagService;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.learning.validator.LearningValidator;
import com.choidh.web.learning.vo.LearningFormVO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping("/professional")
public class ProfessionalController {
    @Autowired private LearningService learningService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private LearningValidator learningValidator;
    @Autowired private TagService tagService;

    @InitBinder("learningFormVO")
    private void initVideoForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(learningValidator);
    }

    @GetMapping("/dashboard")
    public String getProfessional(@CurrentAccount Account account, Model model) {
        // 강의의 수익 차표
        // 강의의 수강생 추이 차표
        // 강의 목록
        // 강의 목록에서 상세로 들어가면 수정할 수 있는 화면
        // 강의 등록
        // 강의 상세에서 공지사항 등록

        // 강의 목록또 프로필의 수강 목록 페이지랑 같은 디자인을 가져와서 사용하는게 나을 듯함.
        // 상세에서는 같은 페이지를 공유해서 사용하는게 나을 것 같긴함.

        // 차트 관련은 라이브러리 끌어다가 쓰고
        // 수익은 판매이력에서 가져다가 그정도만 보여주면 될듯
        // 수강인원은 구매한 인원들 가져다가 쓰면 됨.

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
}
