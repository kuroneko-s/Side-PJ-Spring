package com.choidh.service.kakao.service;

import com.choidh.service.common.AppConstant;
import com.choidh.service.kakao.vo.KakaoPayReadyVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class KakaoPayServiceImplTest {
    private String KAKAO_HOST = "https://open-api.kakaopay.com/online";
    private String ADMIN_KEY = "DEVE7DC6CF6D06D1C5D081EFDF063D15A07A015D";
    private String HOST = "http://localhost:8080";

    @Test
    public void sample() throws Exception {
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
            RestTemplate restTemplate = new RestTemplate();
            KakaoPayReadyVO kakaoPayReadyVO = restTemplate.postForObject(new URI(KAKAO_HOST + "/v1/payment/ready"), body, KakaoPayReadyVO.class);

            assertNotNull(kakaoPayReadyVO);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);

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
}