package com.choidh.service.kakao.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.service.AttachmentService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final AttachmentService attachmentService;

    /**
     * 카카오 페이 결제 초기 진행
     */
    @Override
    public KakaoPayReadyVO kakaoPayReady(){
        // 서버로 요청할 body
        Map<String, String> params = new HashMap<>();
        params.put("cid", "TC0ONETIME"); // Test Code
        params.put("partner_order_id", "1001"); // 가맹점 주문번호
        params.put("partner_user_id", "흑우냥이"); //가맹점 회원
        params.put("item_name", "갤럭시 Tab S9 울라리"); // 상품명
        params.put("quantity", "1"); // 상품 수량
        params.put("total_amount", "2100"); // 상품 총액
        params.put("tax_free_amount", "100"); //상품 비과세 금액
        params.put("approval_url", HOST + AppConstant.KAKAO_PAY_SUCCESS_REDIRECT_URL); // 성공시 redirect URL
        params.put("cancel_url", HOST + AppConstant.KAKAO_PAY_CANCEL_REDIRECT_URL); // 취소시 redirect URL
        params.put("fail_url", HOST + AppConstant.KAKAO_PAY_FAIL_REDIRECT_URL); // 실패시 redirect URL

        // header와 body를 합치는 부분
        HttpEntity<Map<String, String>> body = new HttpEntity<>(params, this.getKakaoHeader());

        try {
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
        Map<String, String> params = new HashMap<>();
        params.put("cid", "TC0ONETIME"); // Test Code
        params.put("tid", tid);
        params.put("partner_order_id", "1001"); // 가맹점 주문번호
        params.put("partner_user_id", "흑우냥이"); //가맹점 회원
        params.put("pg_token", pg_token);

        HttpEntity<Map<String, String>> body = new HttpEntity<>(params, this.getKakaoHeader());

        try{
            return restTemplate.postForObject(new URI(KAKAO_HOST + "/v1/payment/approve"), body, KakaoPayApprovalVO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    @Override
    public KakaoPayCancelVO kakaoPayCancel(String tid){
        Map<String, String> params = new HashMap<>();
        params.put("cid", "TC0ONETIME"); // Test Code
        params.put("tid", tid); // tid
        params.put("cancel_amount", "2100"); // 취소금액
        params.put("cancel_tax_free_amount", "100");//취소금액 세금

        HttpEntity<Map<String, String>> body = new HttpEntity<>(params, this.getKakaoHeader());

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
        headers.add("Authorization", "SECRET_KEY " + ADMIN_KEY);
        headers.add("Content-Type", "application/json");

        return headers;
    }

    /**
     * 강의 구매 승인
     */
    @Override
    @Transactional
    public KakaoPaySuccessResult paidLearning(Long accountId, String learningId) {
        Account account = accountService.getAccountById(accountId);

        // 구매처리한 강의 리스트
        List<Learning> learningList = learningService.getLearningListByIdList(
                Stream.of(learningId.split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList())
        );

        // 구매 이력 등록.
        for (Learning learning : learningList) {
            purchaseHistoryService.regPurchaseHistory(account.getId(), learning.getId());
        }

        // 강의 이미지 목록 조회
        Map<Long, List<String>> learningImageMap = new HashMap<>();
        for (Learning learning : learningList) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
            if (attachmentFiles.size() != 1) {
                throw new IllegalArgumentException();
            }

            List<String> valueList = learningImageMap.getOrDefault(learning.getId(), new ArrayList<>());

            AttachmentFile attachmentFile = attachmentFiles.get(0);
            valueList.add(attachmentFile.getFullPath(""));
            valueList.add(attachmentFile.getOriginalFileName());
            learningImageMap.put(learning.getId(), valueList);
        }

        return KakaoPaySuccessResult.builder()
                .account(account)
                .learningList(learningList)
                .learningImageMap(learningImageMap)
                .build();
    }

    /**
     * 강의 구매 취소
     */
    @Override
    @Transactional
    public KakaoPayCancelResult cancelLearning(Long accountId, String learningId, String kakaoPayTid) {
        Account account = accountService.getAccountById(accountId);

        // 구매 취소한 강의들
        List<Learning> learningList = learningService.getLearningListByIdList(
                Stream.of(learningId.split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList())
        );

        // 구매 이력 취소 처리
        for (Learning learning : learningList) {
            purchaseHistoryService.modPurchaseHistoryOfCancel(account.getId(), learning.getId());
        }

        // 강의 이미지 목록 조회
        Map<Long, List<String>> learningImageMap = new HashMap<>();
        for (Learning learning : learningList) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
            if (attachmentFiles.size() != 1) {
                throw new IllegalArgumentException();
            }

            List<String> valueList = learningImageMap.getOrDefault(learning.getId(), new ArrayList<>());

            AttachmentFile attachmentFile = attachmentFiles.get(0);
            valueList.add(attachmentFile.getFullPath(""));
            valueList.add(attachmentFile.getOriginalFileName());
            learningImageMap.put(learning.getId(), valueList);
        }

        return KakaoPayCancelResult.builder()
                .account(account)
                .learningList(learningList)
                .learningImageMap(learningImageMap)
                .build();
    }
}
