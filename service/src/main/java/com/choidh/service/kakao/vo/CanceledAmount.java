package com.choidh.service.kakao.vo;


import com.choidh.service.common.annotation.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이번 요청으로 취소된 금액
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanceledAmount {
    @Name(name = "전체 취소 금액")
    private Integer total;

    @Name(name = "취소된 비과세 금액")
    private Integer tax_free;

    @Name(name = "취소된 부가세 금액")
    private Integer vat;

    @Name(name = "취소된 포인트 금액")
    private Integer point;

    @Name(name = "취소된 할인 금액")
    private Integer discount;
}
