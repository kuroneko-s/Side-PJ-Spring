package com.choidh.service.menu.vo;

import com.choidh.service.account.vo.AccountType;
import com.choidh.service.annotation.Name;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegMenuVO {
    @Name(name = "이동 URL")
    private String url;

    @Name(name = "메뉴 이름")
    private String name;

    @Name(name = "상위 URL", description = "최상위 메뉴인 경우 ROOT")
    private String parentUrl;

    @Name(name = "하위 메뉴인지 구분용")
    private Integer level;

    @Name(name = "메뉴 순서")
    private Integer menuOrder;

    @Name(name = "접근 가능 유저 타입")
    private List<AccountType> accountTypeList;
}
