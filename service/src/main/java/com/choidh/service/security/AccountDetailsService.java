package com.choidh.service.security;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

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

        return new AccountUser(account);
    }
}
