package com.choidh.service.account.vo.web;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RegAccountVO {
    private String nickname;

    private String email;

    private String password;

    private String passwordcheck;

    public boolean matchPassword() {
        return password.equals(passwordcheck);
    }
}
