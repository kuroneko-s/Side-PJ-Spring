package com.choidh.web.kakao.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.common.StringUtils;
import com.choidh.service.kakao.service.KakaoPayService;
import com.choidh.service.kakao.vo.*;
import com.choidh.service.learning.entity.Learning;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.kakao.vo.KakaoPayForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

import static com.choidh.service.common.AppConstant.getTitle;

/**
 * @see <a href="https://developers.kakaopay.com">카카오페이</a>
 * @see <a href="https://developers.kakao.com/">카카오 API</a>
 */

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    @PostMapping("/kakaoPay")
    public String kakaoPay_Post(@CurrentAccount Account account, KakaoPayForm kakaoPayForm, HttpServletRequest request){
        // 카카오 페이 진행시 필요한 초기 정보 취득.
        KakaoPayReadyVO kakaoPayReadyVO = kakaoPayService.kakaoPayReady();

        // 세션에 필요 정보 저장.
        HttpSession session = request.getSession();

        session.setAttribute(account.getId() + "_learning_id", kakaoPayForm.getLearningIds());
        session.setAttribute(account.getId() + "_tid", kakaoPayReadyVO.getTid());

        // 카카오 페이 주소 위임.
        return "redirect:" + kakaoPayReadyVO.getNext_redirect_pc_url();
    }

    /**
     * 카카오 페이 결제 성공.
     */
    @GetMapping("/kakaopay/success")
    public String kakaoPaySuccess(@CurrentAccount Account account, @RequestParam("pg_token") String pg_token, Model model, HttpServletRequest request) {
        // 검증에 필요한 값을 세션에서 취득.
        HttpSession session = request.getSession();
        String learningId = session.getAttribute(account.getId() + "_learning_id").toString();
        String kakaoPayTid = session.getAttribute(account.getId() + "_tid").toString();

        // TODO : 결제 진행과 성공의 구분이 모호함. 확인 후 구분 필요함. 2024-05-11
        // 결제 진행
        KakaoPayApprovalVO kakaoPayApprovalVO = kakaoPayService.kakaoPayInfo(pg_token, kakaoPayTid);

        session.setAttribute("approvedAt", kakaoPayApprovalVO.getApproved_at());
        session.setAttribute("orderId", kakaoPayApprovalVO.getPartner_order_id());
        session.setAttribute("itemName", kakaoPayApprovalVO.getItem_name());
        session.setAttribute("quantity", kakaoPayApprovalVO.getQuantity());
        session.setAttribute("amountTotal", kakaoPayApprovalVO.getAmount().getTotal());
        session.setAttribute("amountVat", kakaoPayApprovalVO.getAmount().getVat());
        session.setAttribute("paymentType", kakaoPayApprovalVO.getPayment_method_type());
        session.setAttribute(account.getId() + "_learning_id", learningId);
        session.setAttribute("pgToken", pg_token);

        return "redirect:/kakaoPaySuccess";
    }

    /**
     * 카카오 페이 결제 성공 View
     */
    @GetMapping("/kakaoPaySuccess")
    public String getKakaoPaySuccessView(@CurrentAccount Account account, Model model, HttpServletRequest request) {
        // 검증에 필요한 값을 세션에서 취득.
        HttpSession session = request.getSession();
        String approvedAt = session.getAttribute("approvedAt").toString();
        String orderId = session.getAttribute("orderId").toString();
        String itemName = session.getAttribute("itemName").toString();
        String quantity = session.getAttribute("quantity").toString();
        String amountTotal = session.getAttribute("amountTotal").toString();
        String amountVat = session.getAttribute("amountVat").toString();
        String paymentType = session.getAttribute("paymentType").toString();
        String learningId = session.getAttribute(account.getId() + "_learning_id").toString();
        String pgToken = session.getAttribute("pgToken").toString();

        if (StringUtils.isNullOrEmpty(pgToken)) {
            throw new AccessDeniedException("접근 불가");
        }

        KakaoPaySuccessResult kakaoPaySuccessResult = kakaoPayService.paidLearning(account.getId(), learningId);
        List<Learning> learningList = kakaoPaySuccessResult.getLearningList();

        model.addAttribute("account", kakaoPaySuccessResult.getAccount());
        model.addAttribute("learningList", learningList);
        model.addAttribute("learningImageMap", kakaoPaySuccessResult.getLearningImageMap());

        model.addAttribute("approvedAt", approvedAt);
        model.addAttribute("orderId", orderId);
        model.addAttribute("itemName", itemName);
        model.addAttribute("quantity", quantity);
        model.addAttribute("amountTotal", amountTotal);
        model.addAttribute("amountVat", amountVat);
        model.addAttribute("paymentType", paymentType);

        model.addAttribute("pageTitle", getTitle(learningList.get(0).getTitle() + " 외 " + learningList.size()));

        return "cart/success/index";
    }

    /**
     * 카카오 페이 결제 취소
     */
    @GetMapping("/kakaopay/cancel")
    public String kakaoPayCancel(@CurrentAccount Account account, HttpServletRequest request){
        HttpSession session = request.getSession();
        String learningId = session.getAttribute(account.getId() + "_learning_id").toString();
        String kakaoPayTid = session.getAttribute(account.getId() + "_tid").toString();

        // TODO : 문서 확인 필요. 확인 후 구분 필요함. 2024-05-11
        KakaoPayCancelVO kakaoPayCancelVO = kakaoPayService.kakaoPayCancel(kakaoPayTid);

        session.setAttribute("approvedAt", kakaoPayCancelVO.getApproved_at());
        session.setAttribute("canceledAt", kakaoPayCancelVO.getCanceled_at());
        session.setAttribute("orderId", kakaoPayCancelVO.getPartner_order_id());
        session.setAttribute("itemName", kakaoPayCancelVO.getItem_name());
        session.setAttribute("quantity", kakaoPayCancelVO.getQuantity());
        session.setAttribute("amountTotal", kakaoPayCancelVO.getAmount().getTotal());
        session.setAttribute("amountVat", kakaoPayCancelVO.getAmount().getVat());
        session.setAttribute("canceledAmountTotal", kakaoPayCancelVO.getCanceled_amount().getTotal());
        session.setAttribute("canceledAmountVat", kakaoPayCancelVO.getCanceled_amount().getVat());
        session.setAttribute("paymentType", kakaoPayCancelVO.getPayment_method_type());
        session.setAttribute(account.getId() + "_learning_id", learningId);
        session.setAttribute("kakaoPayTid", kakaoPayTid);

        return "redirect:/kakaopayCancel";
    }

    /**
     * 카카오 페이 결제 취소 View
     */
    @GetMapping("/kakaopayCancel")
    public String getKakaoPayCancelView(@CurrentAccount Account account, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String approvedAt = session.getAttribute("approvedAt").toString();
        String canceledAt = session.getAttribute("canceledAt").toString();
        String orderId = session.getAttribute("orderId").toString();
        String itemName = session.getAttribute("itemName").toString();
        String quantity = session.getAttribute("quantity").toString();
        String amountTotal = session.getAttribute("amountTotal").toString();
        String amountVat = session.getAttribute("amountVat").toString();
        String canceledAmountTotal = session.getAttribute("canceledAmountTotal").toString();
        String canceledAmountVat = session.getAttribute("canceledAmountVat").toString();
        String paymentType = session.getAttribute("paymentType").toString();
        String learningId = session.getAttribute(account.getId() + "_learning_id").toString();
        String kakaoPayTid = session.getAttribute("kakaoPayTid").toString();

        if (StringUtils.isNullOrEmpty(kakaoPayTid)) {
            throw new AccessDeniedException("접근 불가");
        }

        KakaoPayCancelResult kakaoPayCancelResult = kakaoPayService.cancelLearning(account.getId(), learningId, kakaoPayTid);
        List<Learning> learningList = kakaoPayCancelResult.getLearningList();

        model.addAttribute("account", kakaoPayCancelResult.getAccount());
        model.addAttribute("learningList", learningList);
        model.addAttribute("learningImageMap", kakaoPayCancelResult.getLearningImageMap());

        model.addAttribute("approvedAt", approvedAt);
        model.addAttribute("canceledAt", canceledAt);
        model.addAttribute("orderId", orderId);
        model.addAttribute("itemName", itemName);
        model.addAttribute("quantity", quantity);
        model.addAttribute("amountTotal", amountTotal);
        model.addAttribute("amountVat", amountVat);
        model.addAttribute("canceledAmountTotal", canceledAmountTotal);
        model.addAttribute("canceledAmountVat", canceledAmountVat);
        model.addAttribute("paymentType", paymentType);

        model.addAttribute("pageTitle", getTitle(learningList.get(0).getTitle() + " 외 " + learningList.size()));

        return "cart/cancel/index";
    }
}
