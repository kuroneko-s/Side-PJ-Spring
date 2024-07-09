package com.choidh.web;

import com.choidh.service.account.entity.Account;
import com.choidh.service.main.service.MainService;
import com.choidh.service.main.vo.HomeVO;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        HomeVO homeVO = mainService.getHomeVO(account);

        model.addAttribute("eventFileList", homeVO.getEventFileList());
        model.addAttribute("learningList", homeVO.getLearningList());
        model.addAttribute("learningImageMap", homeVO.getLearningImageMap());
        model.addAttribute("newLearningList", homeVO.getNewLearningList());
        model.addAttribute("newLearningImageMap", homeVO.getNewLearningImageMap());

        model.addAttribute("pageTitle", getTitle("홈"));

        return "home/index";
    }
}
