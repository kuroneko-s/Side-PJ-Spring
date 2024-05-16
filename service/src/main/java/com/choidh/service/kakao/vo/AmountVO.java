package com.choidh.service.kakao.vo;


import com.choidh.service.annotation.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 결제 금액 정보
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmountVO {
    @Name(name = "전체 결제 금액")
    private Integer total;

    @Name(name = "비과세 금액")
    private Integer tax_free;

    @Name(name = "부가세 금액")
    private Integer vat;

    @Name(name = "사용한 포인트 금액")
    private Integer point;

    @Name(name = "할인 금액")
    private Integer discount;
}
