package com.choidh.web.account.controller;

import com.choidh.service.account.service.AccountService;
import com.choidh.service.account.vo.RegAccountVO;
import com.choidh.web.account.validator.AccountValidator;
import com.choidh.web.account.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountValidator accountValidator;
    private final ModelMapper modelMapper;

    // AccountController 에 Validator 추가.
    @InitBinder("accountVO")
    private void accountVOValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountValidator);
    }

    /**
     * 로그인 View
     */
    @GetMapping("/login")
    public String getLoginView(Model model) {
        model.addAttribute("pageTitle", getTitle("로그인"));
        model.addAttribute("pageContent", "security/login/contents");

        return "security/index";
    }

    /**
     * Get 계정 찾기
     */
    @GetMapping("/findAccount")
    public String getFindAccountView(Model model) {
        model.addAttribute("pageTitle", getTitle("계정 찾기"));
        model.addAttribute("pageContent", "security/find/contents");

        return "security/index";
    }

    /**
     * Get 회원가입 화면
     */
    @GetMapping("/register")
    public String getCreateAccountView(Model model) {
        model.addAttribute("accountVO", new AccountVO());
        model.addAttribute("pageTitle", getTitle("회원가입"));
        model.addAttribute("pageContent", "security/register/registerContents");

        return "security/index";
    }

    /**
     * Post 회원가입 처리
     */
    @PostMapping("/register")
    public String postCreateAccount(@Valid AccountVO accountVO, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("errors", true);
            model.addAttribute("pageTitle", getTitle("회원가입"));
            model.addAttribute("pageContent", "security/register/registerContents");

            return "security/index";
        }

        accountService.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

        model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");
        model.addAttribute("pageTitle", getTitle("메일 인증"));
        model.addAttribute("pageContent", "security/register/registerSuccessContents");

        return "security/index";
    }

    @GetMapping("/test/registerSuccess")
    public String getRegisterSuccessView(Model model) {
        model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");
        model.addAttribute("pageTitle", getTitle("메일 인증"));
        model.addAttribute("pageContent", "security/register/registerSuccessContents");

        return "security/index";
    }
}
