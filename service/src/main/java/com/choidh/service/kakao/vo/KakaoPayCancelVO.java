package com.choidh.service.kakao.vo;


import com.choidh.service.common.annotation.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayCancelVO {
    @Name(name = "요청 고유 번호")
    private String aid;

    @Name(name = "결제 고유 번호, 10자")
    private String tid;

    @Name(name = "가맹점 코드, 20자")
    private String cid;

    @Name(name = "결제 상태",
            description = "READY - 결제 요청" +
                    "SEND_TMS - 결제 요청 메시지(TMS) 발송 완료" +
                    "OPEN_PAYMENT - 사용자가 카카오페이 결제 화면 진입" +
                    "SELECT_METHOD - 결제 수단 선택, 인증 완료" +
                    "ARS_WAITING - ARS 인증 진행 중" +
                    "AUTH_PASSWORD - 비밀번호 인증 완료" +
                    "ISSUED_SID - SID 발급 완료" +
                    "정기 결제 시 SID만 발급 한 경우" +
                    "SUCCESS_PAYMENT - 결제 완료" +
                    "PART_CANCEL_PAYMENT - 부분 취소" +
                    "CANCEL_PAYMENT - 결제된 금액 모두 취소" +
                    "부분 취소 여러 번으로 모두 취소된 경우 포함" +
                    "FAIL_AUTH_PASSWORD - 사용자 비밀번호 인증 실패" +
                    "QUIT_PAYMENT - 사용자가 결제 중단" +
                    "FAIL_PAYMENT - 결제 승인 실패"
    )
    private String status;

    @Name(name = "가맹점 주문번호, 최대 100자")
    private String partner_order_id;

    @Name(name = "가맹점 회원 id, 최대 100자")
    private String partner_user_id;

    @Name(name = "결제 수단, CARD 또는 MONEY 중 하나")
    private String payment_method_type;

    @Name(name = "상품 이름, 최대 100자")
    private String item_name;

    @Name(name = "상품 코드, 최대 100자")
    private String item_code;

    @Name(name = "취소 요청 시 전달한 값")
    private String payload;

    @Name(name = "결제 금액 정보")
    private AmountVO amount;

    @Name(name = "이번 요청으로 취소된 금액")
    private CanceledAmount canceled_amount;

    @Name(name = "남은 취소 가능 금액")
    private CancelAvailableAmount cancel_available_amount;

    @Name(name = "상품 수량")
    private Integer quantity;

    @Name(name = "결제 준비 요청 시각")
    private LocalDateTime created_at;

    @Name(name = "결제 승인 시각")
    private LocalDateTime approved_at;

    @Name(name = "결제 취소 시각")
    private LocalDateTime canceled_at;
}
