package com.choidh.socketserver.security.service;


import com.choidh.service.account.entity.ApiAccount;
import com.choidh.service.account.repository.ApiAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Spring Security 로그인 처리 Service
 */

@Primary
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocketServerDetailsService implements UserDetailsService {
    private final ApiAccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ApiAccount apiAccount = accountRepository.findByEmail(email);

        if (apiAccount == null) throw new UsernameNotFoundException(email);

        return new User(
                apiAccount.getEmail(),
                "",
                Collections.singleton(new SimpleGrantedAuthority(apiAccount.getType().getKey()))
        );
    }
}
