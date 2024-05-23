package com.choidh.web.account.controller;


import com.choidh.web.account.service.AccountService;
import com.choidh.web.account.validator.AccountValidator;
import com.choidh.web.account.vo.AccountVO;
import com.choidh.web.account.vo.EmailTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountValidator accountValidator;
    private final AccountService accountService;

    // AccountController 에 Validator 추가.
    @InitBinder("accountVO")
    private void accountVOValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountValidator);
    }

    /**
     * Get 이메일 인증 화면
     */
    @GetMapping("/mailAuth")
    public String getEmailAuthentication(String token, String email, Model model, RedirectAttributes attributes) {
        return accountService.getMailAuthentication(token, email, model, attributes);
    }

    /**
     * Post 이메일 인증 재전송 처리
     */
    @PostMapping("/mailAuthRetry")
    public String submitReCheckEmailToken(EmailTokenVO emailTokenVO, Model model, RedirectAttributes attributes) {
        return accountService.postMailAuthenticationRetry(emailTokenVO.getEmail(), model, attributes);
    }
}
