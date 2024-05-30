package com.choidh.web;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.service.learning.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final LearningService learningService;

    @GetMapping("/")
    public String indexGet(@CurrentAccount Account account, Model model) {
        List<Learning> learningList;

        if (account != null) {
            learningList = learningService.getTop12LearningListByTag(account.getId());
            model.addAttribute(account);
            model.addAttribute("learningList", learningList);
        } else {
            learningList = learningService.learningOrderByRating();
            model.addAttribute("learningList", learningList);
        }

        List<Learning> learningOrderByDatetime = learningService.learningOrderByCreateLearning();
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
}
