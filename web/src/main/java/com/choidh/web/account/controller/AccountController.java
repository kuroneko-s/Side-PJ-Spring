package com.choidh.web.account.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.account.vo.web.RegAccountVO;
import com.choidh.service.common.utiles.StringUtils;
import com.choidh.service.common.vo.ServiceAppProperties;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.mail.vo.EmailMessageVO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountValidator accountValidator;
    private final ModelMapper modelMapper;
    private final ServiceAppProperties serviceAppProperties;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @InitBinder("accountVO")
    private void accountVOValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountValidator);
    }

    /**
     * Get 로그인 View
     */
    @GetMapping("/login")
    public String getLoginView(Model model) {
        model.addAttribute("pageTitle", getTitle("로그인"));
        model.addAttribute("pageContent", "security/login/contents");

        return "security/index";
    }

    /**
     * Get 회원가입 View
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
        model.addAttribute("pageContent", "security/register/registerAuthenticationMail");

        return "security/index";
    }

    /**
     * Get 계정 찾기 View
     */
    @GetMapping("/findAccount")
    public String getFindAccountView(Model model) {
        model.addAttribute("pageTitle", getTitle("계정 찾기"));
        model.addAttribute("pageContent", "security/find/contents");

        return "security/index";
    }

    /**
     * Post 계정 찾기
     */
    @PostMapping("/findAccount")
    public String postFindAccount(Model model, @RequestParam("email") String email) {
        model.addAttribute("pageTitle", getTitle("계정 찾기"));

        if (StringUtils.isNullOrEmpty(email)) {
            model.addAttribute("message", "메일을 입력해주셔야해요.");

            model.addAttribute("pageContent", "security/find/contents");

            return "security/index";
        }

        Account account = accountService.getAccountByEmail(email);
        if (account == null) {
            model.addAttribute("message", "존재하지 않는 이메일이에요.");

            model.addAttribute("pageContent", "security/find/contents");

            return "security/index";
        }

        // 이메일로 전송 or 그냥 화면으로 띄워주고 끝.
        account.createTokenForEmailForAuthentication();

        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "비밀번호 재설정하기");
        context.setVariable("message", "계정 찾기를 신청하셨습니다.");
        context.setVariable("host", serviceAppProperties.getHost());
        context.setVariable("link", "/password?token=" + account.getTokenForEmailForAuthentication());

        // 비밀번호 재설정 메일 전송
        emailService.sendEmail(EmailMessageVO.builder()
                .to(account.getEmail())
                .subject("비밀번호 재설정하기")
                .message(templateEngine.process("mail/passwordChanged", context))
                .build());

        model.addAttribute("message", "비밀번호 재설정할 수 있는 메일을 보냈어요.");
        model.addAttribute("pageContent", "security/find/findSuccessContents");

        return "security/index";
    }

    /**
     * Get 비밀번호 재설정 View
     */
    @GetMapping("/password")
    public String getPasswordChangeView(Model model) {
        model.addAttribute("pageTitle", getTitle("비밀번호 재설정"));
        model.addAttribute("pageContent", "security/password/contents");

        return "security/index";
    }
}
