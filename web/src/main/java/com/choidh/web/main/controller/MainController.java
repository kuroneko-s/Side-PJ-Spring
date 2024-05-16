package com.choidh.web.main.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.web.main.service.MainService;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;
    private final LearningRepository learningRepository;

    @GetMapping("/")
    public String indexGet(@CurrentAccount Account account, Model model) {
        List<Learning> learningList;

        if (account != null) {
            learningList = learningRepository.findTop4ByTagsOrderByRatingDesc(account.getTags());
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
}
