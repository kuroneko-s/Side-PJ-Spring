package com.choidh.web.kakao.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.kakao.service.KakaoPayService;
import com.choidh.service.kakao.vo.KakaoPayCancelResult;
import com.choidh.service.kakao.vo.KakaoPayReadyVO;
import com.choidh.service.kakao.vo.KakaoPaySuccessResult;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.kakao.vo.KakaoPayForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    @GetMapping("/kakaoPay")
    public void kakaoPay_Get(){

    }

    @PostMapping("/kakaoPay")
    public String kakaoPay_Post(@CurrentAccount Account account, KakaoPayForm kakaoPayForm, HttpServletRequest request){
        // 카카오 페이 진행시 필요한 초기 정보 취득.
        KakaoPayReadyVO kakaoPayReadyVO = kakaoPayService.kakaoPayReady();

        // 세션에 필요 정보 저장.
        HttpSession session = request.getSession();

        session.setAttribute(account.getId() + "_learning_id", kakaoPayForm.getId());
        session.setAttribute(account.getId() + "_tid", kakaoPayReadyVO.getTid());

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
        String learningId = session.getAttribute(account.getId() + "_learning_id").toString();
        String kakaoPayTid = session.getAttribute(account.getId() + "_tid").toString();

        // TODO : 결제 진행과 성공의 구분이 모호함. 확인 후 구분 필요함. 2024-05-11
        // 일단은 성공 기반으로 진행. 실패시 핸들링 필요할지도? 일단 문서 확인해봐야함.
        KakaoPaySuccessResult kakaoPaySuccessResult = kakaoPayService.paidLearning(account.getId(), pg_token, kakaoPayTid, learningId);

        model.addAttribute("account", kakaoPaySuccessResult.getAccount());
        model.addAttribute("info", kakaoPaySuccessResult.getKakaoPayApprovalVO());
        model.addAttribute("learningList", kakaoPaySuccessResult.getLearningList());

        return "cart/paysuccess";
    }

    /**
     * 카카오 페이 결제 취소
     */
    @GetMapping("/kakaoPayCancel")
    public String kakaoPayCancel(@CurrentAccount Account account, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String learningId = session.getAttribute(account.getId() + "_learning_id").toString();
        String kakaoPayTid = session.getAttribute(account.getId() + "_tid").toString();

        // TODO : 문서 확인 필요. 확인 후 구분 필요함. 2024-05-11
        // 일단은 성공 기반으로 진행. 실패시 핸들링 필요할지도? 일단 문서 확인해봐야함.
        KakaoPayCancelResult kakaoPayCancelResult = kakaoPayService.cancelLearning(account.getId(), learningId, kakaoPayTid);

        model.addAttribute("info", kakaoPayCancelResult.getKakaoPayCancelVO());
        model.addAttribute("account", kakaoPayCancelResult.getAccount());
        model.addAttribute("learningList", kakaoPayCancelResult.getLearningList());

        return "cart/cancel";
    }
}
