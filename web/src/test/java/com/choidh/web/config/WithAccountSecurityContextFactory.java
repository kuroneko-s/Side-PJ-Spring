package com.choidh.web.config;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.web.RegAccountVO;
import com.choidh.service.security.service.AccountDetailsService;
import com.choidh.service.account.service.AccountServiceImpl;
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

import java.util.HashSet;

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
        accountVO.setNickname(email.split("@")[0]);
        accountVO.setEmail(email);
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        Account account = accountServiceImpl.regAccount(modelMapper.map(accountVO, RegAccountVO.class));
        account.setChecked(true);
        account.setAccountType(withAccount.accountType());
        account.setTags(new HashSet<>());
        account.setLearningMailNotification(true);
        account.setSiteMailNotification(true);
        account.setLearningWebNotification(true);
        account.setSiteWebNotification(true);

        UserDetails principal = accountDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
                principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
