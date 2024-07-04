package com.choidh.service;

import com.choidh.service.security.vo.AccountUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServiceApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        // EnableJpaAuditing 을 사용해서 사용자 정보를 자동으로 저장해주는 기능을 제공해줄 때의 유저의 정보를 가져오는 방법을 정의
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (
                    authentication == null
                            || !authentication.isAuthenticated()
                            || !(authentication.getPrincipal() instanceof AccountUser)
            ) {
                return Optional.of("anonymousUser");
            }

            AccountUser accountUser = (AccountUser) authentication.getPrincipal();

            return Optional.of(accountUser.getAccount().getId().toString());
        };
    }

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        return modelMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
