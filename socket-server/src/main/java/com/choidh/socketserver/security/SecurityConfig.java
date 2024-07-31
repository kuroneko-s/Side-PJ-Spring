package com.choidh.socketserver.security;

import com.choidh.service.security.handler.CustomAuthenticationFailureHandler;
import com.choidh.socketserver.security.handler.SocketServerSuccessHandler;
import com.choidh.socketserver.security.provider.SocketServerAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final SocketServerSuccessHandler authenticationSuccessHandler;
    private final SocketServerAuthenticationProvider socketServerAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();

        http.formLogin()
                .failureHandler(authenticationFailureHandler)
                .successHandler(authenticationSuccessHandler)
                .permitAll()
            .and()
                .csrf().disable()
                .cors().disable();

        http.authenticationProvider(socketServerAuthenticationProvider);
    }
}
