package com.choidh.web.learning.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.joinTables.service.LearningTagService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.learning.vo.ModLearningVO;
import com.choidh.service.learning.vo.RegLearningVO;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.service.TagService;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.learning.validator.LearningValidator;
import com.choidh.web.learning.vo.LearningFormVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.CREATE_LEARNING;

@Slf4j
@Controller
@RequestMapping(value = "/pro")
@RequiredArgsConstructor
public class LearningProfessionalController {
    private final LearningValidator learningValidator;
    private final LearningService learningService;
    private final TagService tagService;
    private final LearningTagService learningTagService;
    private final AttachmentService attachmentService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @InitBinder("learningFormVO")
    private void initVideoForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(learningValidator);
    }

    // 강의 신규 개설 페이지
    @GetMapping("/profile/learning/create")
    public String getLearningView(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new LearningFormVO());

        return CREATE_LEARNING;
    }

    // 강의 신규 생성
    @PostMapping("/profile/learning/create")
    public String postLearning(@CurrentAccount Account account, Model model,
                               @Valid LearningFormVO learningFormVO, Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("message", "잘못 입력 하셨습니다.");

            return CREATE_LEARNING;
        }

        Learning learning = learningService.regLearning(modelMapper.map(learningFormVO, RegLearningVO.class), account.getId());

        attributes.addFlashAttribute("account", account);
        attributes.addFlashAttribute("message", learning.getTitle() + " 강의가 추가되었습니다");

        return "redirect:/profile/learning/create";
    }

    // 강의 목록 페이지
    @GetMapping("/profile/learning/list")
    public String getLearningListView(@CurrentAccount Account account, Model model) {
        Set<Learning> learningList = learningService.getLearningList(account.getId());

        model.addAttribute("account", account);
        model.addAttribute("learningList", learningList);

        return "profile/learning/list/learning_list";
    }

    // 강의 영상 업로드 페이지
    @GetMapping("/profile/learning/upload/{id}")
    public String getVideoUpdateView(@CurrentAccount Account account, Model model, @PathVariable Long id) throws JsonProcessingException {
        Learning learning = learningService.getLearningById(id);
        List<String> tagList = tagService.getTitleList();

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("tags", tagList);
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));

        return "profile/learning/upload/learning_upload";
    }

    // 강의 편집 페이지. 관리자 전용.
    @GetMapping("/profile/learning/update/{id}")
    public String getUpdateLearningView(@CurrentAccount Account account, Model model, @PathVariable Long id) throws JsonProcessingException {
        Learning learning = learningService.getLearningDetailForUpdate(id);
        List<Tag> tagList = learningTagService.findListByLearningId(id);

        LearningFormVO learningFormVO = LearningFormVO.builder()
                .title(learning.getTitle())
                .price(learning.getPrice())
                .kategorie(learning.getCategoryValue())
                .subscription(learning.getSubscription())
                .simplesubscription(learning.getSimpleSubscription())
                .build();

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("learningFormVO", learningFormVO);
        model.addAttribute("tags", tagList.stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));

        return "learning/update_learning";
    }

    // 강의 편집, 강의 삭제 페이지. 관리자 전용
    @GetMapping("/profile/learning/video/update/{learningId}")
    public String getUpdateLearningFileView(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long id) {
        Learning learning = learningService.getLearningById(id);
        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("fileList", attachmentFiles);

        return "learning/update_learning_video";
    }

    // 비디오 영상 삭제. 관리자용
    // TODO : body 에 실어서 보내는 방식으로 바꿔야할듯. 다중 처리가 안됨.
    // 일단 삭제쿼리를 날리고 실질적으로 이력관리를 하는 용도로 별도 테이블을 생성해서 관리를 해야할수도.
    @DeleteMapping("/profile/video/{learningId}/remove/{videoId}")
    public String delVideo(@CurrentAccount Account account, RedirectAttributes attributes,
                           @PathVariable("learningId") Long learningId, @PathVariable("videoId") Long videoId) {
        learningService.removeVideo(learningId, account.getId(), Collections.singletonList(videoId));

        attributes.addFlashAttribute("account", account);

        return "redirect:/profile/learning/video/update/" + learningId;
    }

    // 강의 수정 처리.
    @PatchMapping("/profile/learning/update/{id}/script")
    public String modLearning(@CurrentAccount Account account, @PathVariable Long id,
                              @Valid LearningFormVO learningFormVO, Errors errors,
                              RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            attributes.addFlashAttribute("message", "값이 잘못되었습니다.");
        } else {
            learningService.modLearning(modelMapper.map(learningFormVO, ModLearningVO.class), account.getId(), id);
            attributes.addFlashAttribute("message", "수정되었습니다.");
        }

        return "redirect:/profile/learning/update/" + id;
    }

    // 강의 open/close 활성화 처리.
    @PostMapping("/profile/learning/start/{id}")
    public String postLearningIsOpening(@CurrentAccount Account account, @PathVariable Long id, RedirectAttributes attributes, @RequestParam("opening") boolean isOpening) {
        learningService.isOpeningLearning(account.getId(), id, isOpening);

        if (isOpening) attributes.addFlashAttribute("message", "강의가 오픈되었습니다.");
        else attributes.addFlashAttribute("message", "강의가 닫혔습니다.");

        return "redirect:/learning/" + id;
    }
}
