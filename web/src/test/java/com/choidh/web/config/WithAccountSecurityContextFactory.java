package com.choidh.web.config;

import com.choidh.service.account.entity.Account;
import com.choidh.service.security.AccountDetailsService;
import com.choidh.web.account.service.AccountServiceImpl;
import com.choidh.web.account.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountServiceImpl accountServiceImpl;
    private final ModelMapper modelMapper;
    private final AccountDetailsService accountDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String email = withAccount.value();

        AccountVO accountVO = new AccountVO();
        accountVO.setNickname("테스트냥이");
        accountVO.setEmail(email);
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        Account account = accountServiceImpl.createAccount(modelMapper.map(accountVO, Account.class));
        account.setTokenChecked(true);

        UserDetails principal = accountDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
                principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
