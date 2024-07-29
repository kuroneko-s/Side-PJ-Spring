package com.choidh.app.common.config;

import com.choidh.app.security.oauth.CustomOAuth2UserService;
import com.choidh.app.security.jwt.JwtAuthFilter;
import com.choidh.app.security.jwt.JwtExceptionFilter;
import com.choidh.app.security.oauth.OAuth2SuccessHandler;
import com.choidh.app.security.jwt.service.JWTService;
import com.choidh.service.account.repository.ApiAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JWTService jwtService;
    private final ApiAccountRepository accountRepository;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    // 바로 접근 가능한 URL
    @Value("${security.accessible.url}")
    private String[] accessibleUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // rest api 설정
        http
                .csrf().disable() // csrf 비활성화 -> cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .cors().disable() // cors 비활성화 -> 프론트와 연결 시 따로 설정 필요
                .httpBasic().disable() // 기본 인증 로그인 비활성화
                .formLogin().disable() // 기본 login form 비활성화
                .logout()
                .logoutSuccessUrl("/");

                // http.headers().frameOptions().disable().disable() // X-Frame-Options 비활성화
//                .headers(c -> c.frameOptions(
//                        HeadersConfigurer.FrameOptionsConfig::disable).disable()) // X-Frame-Options 비활성화

                http.addFilterBefore(new JwtAuthFilter(accountRepository, jwtService), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                        .addFilterBefore(new JwtExceptionFilter(), JwtAuthFilter.class)
                .oauth2Login()
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService);

                http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);// 세션 사용하지 않음

                // request 인증, 인가 설정
                http.authorizeRequests()
                    .mvcMatchers(accessibleUrl).permitAll()
                    .anyRequest().authenticated();
    }
}
