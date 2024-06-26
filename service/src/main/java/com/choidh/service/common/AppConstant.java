package com.choidh.service.common;

/**
 * 전역. 상수 모음.
 */

public class AppConstant {
    /**
     * 카카오 페이 관련 값들
     */
    public static final String KAKAO_PAY_SUCCESS_REDIRECT_URL = "/kakaoPaySuccess"; // 카카오 페이 성공시 리다이렉트 주소
    public static final String KAKAO_PAY_CANCEL_REDIRECT_URL = "/kakaoPayCancel"; // 카카오 페이 취소시 리다이렉트 주소
    public static final String KAKAO_PAY_FAIL_REDIRECT_URL = "/kakaoPaySuccessFail"; // 카카오 페이 실패시 리다이렉트 주소

    // 페이지 URL 관련
    public static final String CREATE_LEARNING = "learning/create_learning";

    public static String getAccountNotFoundErrorMessage(Long id) {
        return id + "에 해당하는 유저를 찾지 못하였습니다.";
    }

    public static String getLearningNotFoundErrorMessage(Long id) {
        return id + "에 해당하는 강의가 존재하지 않습니다.";
    }

    public static String getVideoNotFoundErrorMessage(Long id) {
        return id + "에 해당하는 영상이 존재하지 않습니다.";
    }

    public static String getQuestionNotFoundErrorMessage(Long id) {
        return id + "에 해당하는 질문글이 존재하지 않습니다.";
    }

    public static boolean isNotImageFile(String extension) {
        return !extension.equals("jpg") && !extension.equals("png") && !extension.equals("jpeg");
    }
}
