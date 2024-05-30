package com.choidh.web.account.account;

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
     * Get 계정 찾기
     */
    @GetMapping("/findAccount")
    public String findAccount(Model model) {
        return "findAccount";
    }

    /**
     * Get 회원가입 화면
     */
    @GetMapping("/register")
    public String getCreateAccountView(Model model) {
        model.addAttribute("accountVO", new AccountVO());

        return "register";
    }

    /**
     * Post 회원가입 처리
     */
    @PostMapping("/register")
    public String postCreateAccount(@Valid AccountVO accountVO, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("errors", true);
            return "register";
        }

        accountService.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

        model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");

        return "registerSuccess";
    }
}
