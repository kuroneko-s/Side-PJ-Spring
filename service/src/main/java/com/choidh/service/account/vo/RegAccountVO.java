package com.choidh.service.account.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class RegAccountVO {
    private String nickname;

    private String email;

    private String password;

    private String passwordcheck;
}
