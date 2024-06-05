package com.choidh.service.kakao.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.common.AppConstant;
import com.choidh.service.kakao.vo.*;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.purchaseHistory.service.PurchaseHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 카카오 페이 API (<a href="https://developers.kakao.com/product/kakaoPay">카카오 페이</a>)
 */

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoPayServiceImpl implements KakaoPayService {
    @Value("${kakao.host}") private String KAKAO_HOST;
    @Value("${kakao.admin.key}") private String ADMIN_KEY;
    @Value("${kakao.redirect.host}") private String HOST;

    private final RestTemplate restTemplate;
    private final AccountService accountService;
    private final LearningService learningService;
    private final PurchaseHistoryService purchaseHistoryService;

    /**
     * 카카오 페이 결제 초기 진행
     */
    @Override
    public KakaoPayReadyVO kakaoPayReady(){
        // 서버로 요청할 body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME"); // Test Code
        params.add("partner_order_id", "1001"); // 가맹점 주문번호
        params.add("partner_user_id", "흑우냥이"); //가맹점 회원
        params.add("item_name", "갤럭시 Tab S9 울라리"); // 상품명
        params.add("quantity", "1"); // 상품 수량
        params.add("total_amount", "2100"); // 상품 총액
        params.add("tax_free_amount", "100"); //상품 비과세 금액
        params.add("approval_url", HOST + AppConstant.KAKAO_PAY_SUCCESS_REDIRECT_URL); // 성공시 redirect URL
        params.add("cancel_url", HOST + AppConstant.KAKAO_PAY_CANCEL_REDIRECT_URL); // 취소시 redirect URL
        params.add("fail_url", HOST + AppConstant.KAKAO_PAY_FAIL_REDIRECT_URL); // 실패시 redirect URL

        // header와 body를 합치는 부분
        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, this.getKakaoHeader());

        try{
            return restTemplate.postForObject(new URI(KAKAO_HOST + "/v1/payment/ready"), body, KakaoPayReadyVO.class);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);

            throw new RuntimeException(e);
        }
    }

    /**
     * 카카오 페이 진행 검증
     */
    @Override
    public KakaoPayApprovalVO kakaoPayInfo(String pg_token, String tid){
        //서버로 요청할 body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME"); // Test Code
        params.add("tid", tid);
        params.add("partner_order_id", "1001"); // 가맹점 주문번호
        params.add("partner_user_id", "흑우냥이"); //가맹점 회원
        params.add("pg_token", pg_token);

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, this.getKakaoHeader());

        try{
            return restTemplate.postForObject(new URI(KAKAO_HOST + "/v1/payment/approve"), body, KakaoPayApprovalVO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    @Override
    public KakaoPayCancelVO kakaoPayCancel(String tid){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME"); // Test Code
        params.add("tid", tid); // tid
        params.add("cancel_amount", "2100"); // 취소금액
        params.add("cancel_tax_free_amount", "100");//취소금액 세금

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, this.getKakaoHeader());

        try{
            return restTemplate.postForObject(new URI(KAKAO_HOST + "/v1/payment/cancel"), body, KakaoPayCancelVO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    /**
     * 서버로 요청할 header
     */
    private HttpHeaders getKakaoHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + ADMIN_KEY);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");

        return headers;
    }

    /**
     * 강의 구매 승인
     */
    @Override
    @Transactional
    public KakaoPaySuccessResult paidLearning(Long accountId, String pgToken, String kakaoPayTid, String learningId) {
        KakaoPaySuccessResult kakaoPaySuccessResult = new KakaoPaySuccessResult();
        Account account = accountService.getAccountById(accountId);
        kakaoPaySuccessResult.setAccount(account);

        // 결제 진행
        KakaoPayApprovalVO kakaoPayApprovalVO = this.kakaoPayInfo(pgToken, kakaoPayTid);
        kakaoPaySuccessResult.setKakaoPayApprovalVO(kakaoPayApprovalVO);

        // 구매처리한 강의 리스트
        List<Learning> learningList = learningService.getLearningListByIdList(
                Stream.of(learningId.split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList())
        );
        kakaoPaySuccessResult.setLearningList(Collections.unmodifiableList(learningList));

        // 구매 이력 등록.
        for (Learning learning : learningList) {
            purchaseHistoryService.regPurchaseHistory(account.getId(), learning.getId());
        }

        return kakaoPaySuccessResult;
    }

    /**
     * 강의 구매 취소
     */
    @Override
    @Transactional
    public KakaoPayCancelResult cancelLearning(Long accountId, String learningId, String kakaoPayTid) {
        KakaoPayCancelResult kakaoPayCancelResult = new KakaoPayCancelResult();
        Account account = accountService.getAccountById(accountId);
        kakaoPayCancelResult.setAccount(account);

        // 구매 취소 진행
        KakaoPayCancelVO kakaoPayCancelVO = this.kakaoPayCancel(kakaoPayTid);
        kakaoPayCancelResult.setKakaoPayCancelVO(kakaoPayCancelVO);

        // 구매 취소한 강의들
        List<Learning> learningList = learningService.getLearningListByIdList(
                Stream.of(learningId.split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList())
        );
        kakaoPayCancelResult.setLearningList(Collections.unmodifiableList(learningList));

        // 구매 이력 취소 처리
        for (Learning learning : learningList) {
            purchaseHistoryService.modPurchaseHistoryOfCancel(account.getId(), learning.getId());
        }

        return kakaoPayCancelResult;
    }
}
