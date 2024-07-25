package com.choidh.app.common.config;

import com.choidh.service.security.handler.CustomAuthenticationFailureHandler;
import com.choidh.service.security.handler.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    // 바로 접근 가능한 URL
    @Value("${security.accessible.url}")
    private String[] accessibleUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll();
//                .mvcMatchers(accessibleUrl).permitAll()
//                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login")
                .failureHandler(authenticationFailureHandler)
                .successHandler(authenticationSuccessHandler)
                .permitAll()
                .and()
                .requestCache()
                .requestCache(new HttpSessionRequestCache());

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll();

        http.rememberMe().disable();
    }

}
