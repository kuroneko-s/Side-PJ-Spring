package com.choidh.web.learning.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.tag.service.TagService;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.tag.vo.DelTagForm;
import com.choidh.web.tag.vo.RegTagForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class LearningRestController {
    private final LearningService learningService;
    private final CartService cartService;
    private final TagService tagService;

    // 영상 파일 저장.
    @PostMapping(value = "/profile/learning/upload/{id}/video", produces="text/plain;Charset=UTF-8")
    public ResponseEntity videoUpdate(@CurrentAccount Account account, @PathVariable Long id,
                                                    MultipartHttpServletRequest httpServletRequest) {
        List<MultipartFile> multipartFileList = httpServletRequest.getFiles("videofile");
        if (multipartFileList.isEmpty()) return ResponseEntity.badRequest().build();

        try {
            learningService.fileUpdate(
                    multipartFileList,
                    account,
                    id,
                    AttachmentFileType.VIDEO
            );

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return ResponseEntity.badRequest().build();
        }
    }

    // 강의 태그 추가. API.
    @PostMapping("/profile/learning/upload/{id}/add")
    public ResponseEntity postTagsForLearning(@CurrentAccount Account account, @PathVariable Long id, @RequestBody RegTagForm regTagForm) {
        try {
            tagService.addTagsForLearning(regTagForm.getTitle(), id);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 강의 태그 삭제. API.
    @PostMapping("/profile/learning/upload/{id}/remove")
    public ResponseEntity learningRemoveTags(@CurrentAccount Account account, @PathVariable Long id, @RequestBody DelTagForm delTagForm) {
        return tagService.removeTagsForLearning(delTagForm.getLearningTagJoinTableId()) <= 0
                ? ResponseEntity.badRequest().build()
                : ResponseEntity.ok().build();
    }

    // 강의 배너 이미지 업로드. API.
    @PostMapping(value = "/profile/learning/upload/{id}/banner", produces="text/plain;Charset=UTF-8")
    public ResponseEntity bannerUpdate(@CurrentAccount Account account, @PathVariable Long id,
                                       @RequestParam("banner") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) return ResponseEntity.badRequest().build();

        try {
            learningService.fileUpdate(
                    Collections.singletonList(multipartFile),
                    account,
                    id,
                    AttachmentFileType.BANNER
            );

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return ResponseEntity.badRequest().build();
        }
    }

    // 강의를 카트에 추가. API.
    @GetMapping("/learning/{learningId}/cart/add")
    public ResponseEntity cartLearning(@CurrentAccount Account account, @PathVariable("learningId") Long id) {
        try {
            cartService.addCart(id, account.getId());

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            log.error(e.getMessage(), e);

            return ResponseEntity.badRequest().build();
        }
    }
}
