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
public class KakaoPayApprovalVO {
    @Name(name = "요청 고유 번호")
    private String aid;

    @Name(name = "결제 고유 번호")
    private String tid;

    @Name(name = "가맹점 코드")
    private String cid;

    @Name(name = "정기결제용 ID, 정기결제 CID로 단건결제 요청 시 발급")
    private String sid;

    @Name(name = "가맹점 주문번호, 최대 100자")
    private String partner_order_id;

    @Name(name = "가맹점 회원 id, 최대 100자")
    private String partner_user_id;

    @Name(name = "결제 수단, CARD 또는 MONEY 중 하나")
    private String payment_method_type;

    @Name(name = "결제 금액 정보")
    private AmountVO amount;

    @Name(name = "결제 상세 정보, 결제수단이 카드일 경우만 포함")
    private CardVO card_info;

    @Name(name = "상품 이름, 최대 100자")
    private String item_name;

    @Name(name = "상품 코드, 최대 100자")
    private String item_code;

    @Name(name = "결제 승인 요청에 대해 저장한 값, 요청 시 전달된 내용")
    private String payload;

    @Name(name = "상품 수량")
    private Integer quantity;

    @Name(name = "상품 비과세 금액")
    private Integer tax_free_amount;

    @Name(name = "")
    private Integer vat_amount;

    @Name(name = "결제 준비 요청 시각")
    private LocalDateTime created_at;

    @Name(name = "결제 승인 시각")
    private LocalDateTime approved_at;
}
