package com.choidh.service.account.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ModAccountVO {
    private String nickname;

    private String description;
}
