package com.choidh.service.security.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.vo.web.AccountType;
import com.choidh.service.security.vo.AccountUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security 로그인 처리 Service
 */

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmailAndChecked(email, true);

        if (account == null) {
            account = accountRepository.findByEmailAndChecked(email, false);

            if (account != null) {
                throw new CredentialsExpiredException(email);
            } else {
                throw new UsernameNotFoundException(email);
            }
        }

        List<SimpleGrantedAuthority> roleUser = new ArrayList<>();
        roleUser.add(new SimpleGrantedAuthority(AccountType.USER.getKey()));

        if (account.getAccountType().equals(AccountType.PROFESSIONAL)) {
            roleUser.add(new SimpleGrantedAuthority(AccountType.PROFESSIONAL.getKey()));
        }

        if (account.getAccountType().equals(AccountType.ADMIN)) {
            roleUser.add(new SimpleGrantedAuthority(AccountType.PROFESSIONAL.getKey()));
            roleUser.add(new SimpleGrantedAuthority(AccountType.ADMIN.getKey()));
        }

        return new AccountUser(account, roleUser);
    }
}
