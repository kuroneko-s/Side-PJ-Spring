package com.choidh.web.common.config;

import com.choidh.service.security.AccountDetailsService;
import com.choidh.service.security.CustomAuthenticationFailureHandler;
import com.choidh.service.security.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final DataSource dataSource;
    private final AccountDetailsService accountDetailsService;
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    // 바로 접근 가능한 URL
    @Value("${security.accessible.url}")
    private String[] accessibleUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(accessibleUrl).permitAll()
                .mvcMatchers("/learning/search/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/learning/{id}").access("@webSecurity.checkLearningId(#id)")
                .anyRequest().authenticated();

        http.formLogin()
            .loginPage("/login")
            .failureHandler(authenticationFailureHandler)
            .successHandler(new AuthenticationSuccessHandler() {
                @Override
                public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                    redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");
                }
            })
            .permitAll();

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll();

        http.rememberMe()
                .userDetailsService(accountDetailsService)
                .tokenRepository(tokenRepository())
                .tokenValiditySeconds(60 * 30); // 자동 로그인 30분
    }

    @Bean
    public PersistentTokenRepository tokenRepository(){
        // remember-me 사용을 위한 저장소
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .mvcMatchers("/static/video/**")
                .mvcMatchers("/uploadFiles/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
