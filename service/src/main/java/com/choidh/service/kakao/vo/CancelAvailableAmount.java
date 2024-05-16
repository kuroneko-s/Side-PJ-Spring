package com.choidh.service.kakao.vo;


import com.choidh.service.annotation.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelAvailableAmount {
    @Name(name = "전체 취소 가능 금액")
    private Integer total;

    @Name(name = "취소 가능한 비과세 금액")
    private Integer tax_free;

    @Name(name = "취소 가능한 부가세 금액")
    private Integer vat;

    @Name(name = "취소 가능한 포인트 금액")
    private Integer point;

    @Name(name = "취소 가능한 할인 금액")
    private Integer discount;
}
