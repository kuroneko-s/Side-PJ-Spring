package com.choidh.socketserver.security;

import com.choidh.service.account.repository.ApiAccountRepository;
import com.choidh.service.jwt.JWTService;
import com.choidh.service.security.handler.CustomAuthenticationFailureHandler;
import com.choidh.socketserver.security.handler.SocketServerSuccessHandler;
import com.choidh.socketserver.security.provider.SocketServerAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final SocketServerSuccessHandler authenticationSuccessHandler;
    private final SocketServerAuthenticationProvider socketServerAuthenticationProvider;
    private final JWTService jwtService;
    private final ApiAccountRepository accountRepository;

    @Value("${security.accessible.url}")
    private String[] accessibleUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(accessibleUrl).permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .failureHandler(authenticationFailureHandler)
                .successHandler(authenticationSuccessHandler)
                .permitAll()
            .and()
                .csrf().disable()
                .cors().disable();

        http.addFilterBefore(new JwtAuthFilter(accountRepository, jwtService), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
        http.authenticationProvider(socketServerAuthenticationProvider);
    }
}
