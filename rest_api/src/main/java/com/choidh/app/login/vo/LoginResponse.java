package com.choidh.app.login.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;

    private Long expiration;
}
