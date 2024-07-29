package com.choidh.service.account.vo.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public enum AccountType {
    ADMIN("ROLE_ADMIN"),
    PROFESSIONAL("ROLE_PROFESSIONAL"),
    USER("ROLE_USER");

    private final String key;
}
