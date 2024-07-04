package com.choidh.web.common.annotation;

import com.choidh.service.security.vo.AccountUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Spring Security Principal 자동 주입 애노테이션 {@link AuthenticationPrincipal}
 * AuthenticationPrincipal#expression는 Principal 객체에 직접적으로 접근하는 SpEL. account 는 {@link AccountUser} 의 필드명
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentAccount {
}
