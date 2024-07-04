package com.choidh.service.config;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.security.service.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {
    private final AccountRepository accountRepository;
    private final AccountDetailsService accountDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String email = withAccount.value();

        Account account = new Account();
        account.setNickname("테스트냥이");
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode("1234567890"));
        account.setChecked(true);
        account.createTokenForEmailForAuthentication();
        accountRepository.save(account);

        Cart cart = cartService.regCart(account.getId());
        account.setCart(cart);

        UserDetails principal = accountDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
                principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
