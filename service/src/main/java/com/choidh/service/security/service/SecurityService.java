package com.choidh.service.security.service;

import com.choidh.service.account.entity.Account;

public interface SecurityService {
    /**
     * 강제 로그인 처리.
     */
    void login(Account account);
}
