package com.choidh.service.kakao.vo;


import com.choidh.service.annotation.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 결제 상세 정보
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardVO {
    @Name(name = "매입 카드사 한글명")
    private String purchase_corp;

    @Name(name = "매입 카드사 코드")
    private String purchase_corp_code;

    @Name(name = "카드 발급사 한글명")
    private String issuer_corp;

    @Name(name = "카드 발급사 코드")
    private String issuer_corp_code;

    @Name(name = "카카오페이 매입사명")
    private String kakaopay_purchase_corp;

    @Name(name = "카카오페이 매입사 코드")
    private String kakaopay_purchase_corp_code;

    @Name(name = "카카오페이 발급사명")
    private String kakaopay_issuer_corp;

    @Name(name = "카카오페이 발급사 코드")
    private String kakaopay_issuer_corp_code;

    @Name(name = "카드 BIN")
    private String bin;

    @Name(name = "카드 타입")
    private String card_type;

    @Name(name = "할부 개월 수")
    private String install_month;

    @Name(name = "카드사 승인번호")
    private String approved_id;

    @Name(name = "카드사 가맹점 번호")
    private String card_mid;

    @Name(name = "무이자할부 여부(Y/N)")
    private String interest_free_install;

    @Name(name = "카드 상품 코드")
    private String card_item_code;
}
