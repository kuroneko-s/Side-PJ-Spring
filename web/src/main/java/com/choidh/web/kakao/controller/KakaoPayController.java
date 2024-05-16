package com.choidh.web.kakao.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.web.account.service.AccountService;
import com.choidh.service.kakao.service.KakaoPayService;
import com.choidh.service.kakao.vo.KakaoPayApprovalVO;
import com.choidh.web.kakao.vo.KakaoPayForm;
import com.choidh.service.kakao.vo.KakaoPayReadyVO;
import com.choidh.web.learning.service.LearningService;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;
    private final LearningService learningService;
    private final AccountService accountService;

    @GetMapping("/kakaoPay")
    public void kakaoPay_Get(){

    }

    @PostMapping("/kakaoPay")
    public String kakaoPay_Post(@CurrentAccount Account account, KakaoPayForm kakaoPayForm, HttpServletRequest request){
        // 카카오 페이 진행시 필요한 초기 정보 취득.
        KakaoPayReadyVO kakaoPayReadyVO = kakaoPayService.kakaoPayReady();

        // 세션에 필요 정보 저장.
        HttpSession session = request.getSession();
        session.setAttribute(account.getId() + "_id_str", kakaoPayForm.getId());
        session.setAttribute(account.getId() + "_lecture_str", kakaoPayForm.getLecture());
        session.setAttribute(account.getId() + "_tid_str", kakaoPayReadyVO.getTid());

        // 카카오 페이 주소 위임.
        return "redirect:" + kakaoPayReadyVO.getNext_redirect_pc_url();
    }

    /**
     * 카카오 페이 결제 성공
     */
    @GetMapping("/kakaoPaySuccess")
    public String kakaoPaySuccess(@CurrentAccount Account account, @RequestParam("pg_token") String pg_token, Model model, HttpServletRequest request) {
        // 검증에 필요한 값을 세션에서 취득.
        HttpSession session = request.getSession();
        String kakaoPayTid = session.getAttribute(account.getId() + "_id_str").toString();

        // TODO : 결제 진행과 성공의 구분이 모호함. 확인 후 구분 필요함. 2024-05-11
        // 결제 진행
        KakaoPayApprovalVO kakaoPayApprovalVO = kakaoPayService.kakaoPayInfo(pg_token, kakaoPayTid);

        List<Learning> learningList = getLearningByIdAndLecture(kakaoPayTid, session.getAttribute(account.getId() + "_lecture_str").toString());
        Account newAccount = accountService.buyLearningSuccessful(account.getId(), learningList);

        model.addAttribute("account", newAccount);
        model.addAttribute("info", kakaoPayApprovalVO);
        model.addAttribute("learningList", learningList);

        return "shop/paysuccess";
    }

    /**
     * 카카오 페이 결제 취소
     */
    @GetMapping("/kakaoPayCancel")
    public String kakaoPayCancel(@CurrentAccount Account account, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String tid = session.getAttribute(account.getId() + "_tid_str").toString();

        List<Learning> learningList = getLearningByIdAndLecture(tid, session.getAttribute(account.getId() + "_lecture_str").toString());
        Account newAccount = accountService.removeListenLearning(account.getId(), learningList);

        model.addAttribute("info", kakaoPayService.kakaoPayCancel(tid));
        model.addAttribute("account", newAccount);
        model.addAttribute("learningList", learningList);

        return "shop/cancel";
    }

    private List<Learning> getLearningByIdAndLecture(String id, String lecture) {
        List<String> id_split = List.of(id.split(","));
        List<String> lecture_split = List.of(lecture.split(","));

        if(id_split.size() != lecture_split.size()){
            throw new DuplicateKeyException(id);
        }

        return learningService.findLearningByIdAndLecture(id_split, lecture_split);
    }

}
