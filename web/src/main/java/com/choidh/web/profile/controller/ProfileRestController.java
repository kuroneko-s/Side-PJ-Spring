package com.choidh.web.profile.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.joinTables.service.AccountTagService;
import com.choidh.service.tag.vo.RegTagVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.tag.validator.TagFormValidator;
import com.choidh.web.tag.vo.TagForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileRestController {
    private final AccountTagService accountTagService;
    private final TagFormValidator tagFormValidator;

    @InitBinder("tagForm")
    private void tagFormValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(tagFormValidator);
    }

    /**
     * POST 계정 태그 추가
     */
    @PostMapping("/update/tags/add")
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError("title").getDefaultMessage());
        }

        AccountTagJoinTable accountTagJoinTable = accountTagService.regTag(
                RegTagVO.builder()
                        .title(tagForm.getTitle())
                        .build(),
                account.getId());

        if (accountTagJoinTable == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * POST 계정 태그 삭제
     */
    @PostMapping("/update/tags/remove")
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody Long tagId) {
        if (tagId == null) {
            return ResponseEntity.badRequest().build();
        }

        if (accountTagService.delTag(tagId, account.getId())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
