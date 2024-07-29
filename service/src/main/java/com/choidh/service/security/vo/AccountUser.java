package com.choidh.service.security.vo;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.web.AccountType;
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
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority(AccountType.USER.getKey())));
        this.account = account;
    }

    public AccountUser(Account account, List<SimpleGrantedAuthority> userRoleList) {
        super(account.getNickname(), account.getPassword(), userRoleList);
        this.account = account;
    }
}
