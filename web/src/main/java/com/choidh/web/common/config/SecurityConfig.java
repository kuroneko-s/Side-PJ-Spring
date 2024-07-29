package com.choidh.web.common.config;

import com.choidh.service.account.vo.web.AccountType;
import com.choidh.service.security.handler.CustomAuthenticationFailureHandler;
import com.choidh.service.security.handler.CustomAuthenticationSuccessHandler;
import com.choidh.service.security.service.AccountDetailsService;
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final DataSource dataSource;
    private final AccountDetailsService accountDetailsService;
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    // 바로 접근 가능한 URL
    @Value("${security.accessible.url}")
    private String[] accessibleUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(accessibleUrl).permitAll()
                .mvcMatchers("/learning/search/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/event/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/learning/{id}").access("@webSecurity.checkLearningId(#id)")
                .mvcMatchers("/admin/**").hasAuthority(AccountType.ADMIN.getKey())
                .mvcMatchers("/professional/**").hasAnyAuthority(AccountType.ADMIN.getKey(), AccountType.PROFESSIONAL.getKey())
                .anyRequest().authenticated();

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
