package com.choidh.web.main.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.web.account.service.AccountService;
import com.choidh.web.account.validator.AccountValidator;
import com.choidh.web.account.vo.AccountVO;
import com.choidh.web.account.vo.EmailTokenVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.main.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;
    private final LearningRepository learningRepository;
    private final AccountService accountService;
    private final AccountValidator accountValidator;

    // AccountController 에 Validator 추가.
    @InitBinder("accountVO")
    private void accountVOValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountValidator);
    }

    @GetMapping("/")
    public String indexGet(@CurrentAccount Account account, Model model) {
        List<Learning> learningList;

        if (account != null) {
            learningList = learningRepository.findTop12ByTagsOrderByRatingDesc(account.getTags());
            model.addAttribute(account);
            model.addAttribute("learningList", learningList);
        } else {
            learningList = mainService.learningOrderByRating();
            model.addAttribute("learningList", learningList);
        }

        List<Learning> learningOrderByDatetime = mainService.learningOrderByCreateLearning();
        model.addAttribute("learningOrderByDatetime", learningOrderByDatetime);

        return "index";
    }

    @PostMapping("/")
    public String indexPost(@CurrentAccount Account account, Model model) {
        if (account != null) model.addAttribute(account);

        return "index";
    }

    @GetMapping("/login")
    public String loginGet() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginPost(RedirectAttributes attributes) {
        attributes.addFlashAttribute("message", "로그인 정보가 없습니다. 계정을 확인 해주세요.");

        return "redirect:/login";
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

        accountService.postCreateAccount(accountVO);

        model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");

        return "registerSuccess";
    }

    // 테스트용.
    //    @GetMapping("/registerSuccess")
    public String registerSuccess(Model model) {
        model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");

        return "registerSuccess";
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
