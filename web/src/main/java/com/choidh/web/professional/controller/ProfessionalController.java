package com.choidh.web.professional.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping("/professional")
public class ProfessionalController {
    @Autowired
    private LearningService learningService;

    @GetMapping("/dashboard")
    public String getProfessional(@CurrentAccount Account account, Model model) {
        // 강의의 수익 차표
        // 강의의 수강생 추이 차표
        // 강의 목록
        // 강의 목록에서 상세로 들어가면 수정할 수 있는 화면
        // 강의 등록
        // 강의 상세에서 공지사항 등록

        // 강의 목록또 프로필의 수강 목록 페이지랑 같은 디자인을 가져와서 사용하는게 나을 듯함.
        // 상세에서는 같은 페이지를 공유해서 사용하는게 나을 것 같긴함.

        // 차트 관련은 라이브러리 끌어다가 쓰고
        // 수익은 판매이력에서 가져다가 그정도만 보여주면 될듯
        // 수강인원은 구매한 인원들 가져다가 쓰면 됨.

        model.addAttribute("pageTitle", getTitle(account.getNickname() + "님의 대시보드"));
        model.addAttribute("pageContent", "professional/contents");

        return "professional/index";
    }

    /**
     * Get 강의 목록 View
     */
    @GetMapping("/learning/list")
    public String getLearningListView(@CurrentAccount Account account, Model model) {
        model.addAttribute("pageTitle", getTitle(account.getNickname() + "님의 강의 목록"));
        model.addAttribute("pageContent", "professional/learning/list/contents");

        // 등록 / 수정 / 삭제(비활성화) 가능하도록
        return "professional/index";
    }

    /**
     * Get 강의 등록 View
     */
    @GetMapping("/learning/create")
    public String getRegLearningView(@CurrentAccount Account account, Model model) {
        model.addAttribute("pageTitle", getTitle("강의 등록"));
        model.addAttribute("pageContent", "professional/learning/create/contents");

        return "professional/index";
    }

    /**
     * Get 강의 수정 View
     */
    @GetMapping("/learning/{learningId}")
    public String getLearningModifyView(@CurrentAccount Account account, @PathVariable Long learningId, Model model) {
        Learning learning = learningService.getLearningById(learningId);

        model.addAttribute("pageTitle", getTitle(learning.getTitle() + "강의 수정"));
        model.addAttribute("pageContent", "professional/learning/modify/contents");

        return "professional/index";
    }
}
