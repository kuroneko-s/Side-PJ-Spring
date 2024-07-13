package com.choidh.web.config;

import com.choidh.service.account.vo.AccountType;
import com.choidh.service.security.vo.AccountRoleType;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContextFactory.class)
public @interface WithAccount {

    String value();

    AccountType accountType() default AccountType.USER;
}
