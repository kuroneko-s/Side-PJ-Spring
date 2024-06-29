package com.choidh.service.security;


import com.choidh.service.account.entity.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * Spring Security Principal 정의
 */

@Getter
public class AccountUser extends User {
    private final Account account;

    public AccountUser(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority(AccountRoleType.ROLE_USER.name())));
        this.account = account;
    }

    public AccountUser(Account account, List<SimpleGrantedAuthority> userRoleList) {
        super(account.getNickname(), account.getPassword(), userRoleList);
        this.account = account;
    }
}
