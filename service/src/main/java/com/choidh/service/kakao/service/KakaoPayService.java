package com.choidh.service.kakao.service;

import com.choidh.service.kakao.vo.*;

public interface KakaoPayService {
    /**
     * 카카오 페이 진행시 필요한 초기 정보 취득
     */
    KakaoPayReadyVO kakaoPayReady();

    /**
     * 카카오 페이 진행 검증
     */
    KakaoPayApprovalVO kakaoPayInfo(String pg_token, String tid);

    /**
     * 카카오 페이 결제 취소
     */
    KakaoPayCancelVO kakaoPayCancel(String tid);

    /**
     * 강의 구매
     */
    KakaoPaySuccessResult paidLearning(Long accountId, String pgToken, String kakaoPayTid, String learningId);

    /**
     * 강의 구매 취소 처리
     */
    KakaoPayCancelResult cancelLearning(Long accountId, String learningId, String kakaoPayTid);

}
