package com.choidh.web.mail.controller;

import com.choidh.service.account.service.AccountService;
import com.choidh.web.mail.validator.EmailValidator;
import com.choidh.web.mail.vo.EmailFormVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EmailController {
    private final AccountService accountService;
    private final EmailValidator emailValidator;

    @InitBinder("emailFormVO")
    private void accountVOValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(emailValidator);
    }

    /**
     * Get 이메일 재인증 View.
     */
    @GetMapping("/registerSuccess")
    public String getEmailReAuthenticationView(Model model) {
        model.addAttribute("pageTitle", getTitle("이메일 재인증"));
        model.addAttribute("pageContent", "security/register/registerAuthenticationMail");

        return "security/index";
    }

    /**
     * Get 이메일 인증 처리.
     */
    @GetMapping("/mailAuth")
    public String getEmailAuthentication(String token, String email, Model model, RedirectAttributes attributes) {
        boolean isSuccess = accountService.verifyingEmail(token, email);

        if (isSuccess) {
            attributes.addFlashAttribute("success", "인증이 완료되었습니다.");

            return "redirect:/login";
        }

        model.addAttribute("message", "인증 주소가 잘못 되었습니다. 다시 신청해주시거나, 재전송 버튼을 눌러주세요");
        model.addAttribute("email", email);
        model.addAttribute(new EmailFormVO());

        model.addAttribute("pageTitle", getTitle("이메일 재인증"));
        model.addAttribute("pageContent", "security/register/registerAuthenticationMail");

        return "security/index";
    }

    /**
     * Post 이메일 인증 재전송 처리
     */
    @PostMapping("/mailAuthRetry")
    public String submitReCheckEmailToken(EmailFormVO emailFormVO, Errors errors, Model model) {
        String email = emailFormVO.getEmail();
        if (email == null || email.isEmpty() || errors.hasErrors()) {
            model.addAttribute("message", "메일이 옳바르지 않습니다. 다시 입력해주세요.");
            model.addAttribute("pageContent", "security/register/registerAuthenticationMail");

            return "security/index";
        }

        boolean isSuccess = accountService.sendEmailForAuthentication(email);

        if (isSuccess) {
            model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");
        } else {
            model.addAttribute("message", "인증용 메일은 1시간에 한번만 전송이 가능합니다. 양해 부탁드립니다");
        }

        model.addAttribute("pageContent", "security/register/registerAuthenticationMail");

        return "security/index";
    }
}
