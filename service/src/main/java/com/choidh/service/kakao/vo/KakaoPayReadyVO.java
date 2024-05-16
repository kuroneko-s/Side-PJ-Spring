package com.choidh.service.kakao.vo;


import com.choidh.service.annotation.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayReadyVO {
    @Name(name = "결제 고유 번호, 20자")
    private String tid;

    @Name(name = "요청한 클라이언트가 PC 웹일 경우")
    private String next_redirect_pc_url;

    @Name(name = "결제 준비 요청 시간")
    private Date created_at;
}
