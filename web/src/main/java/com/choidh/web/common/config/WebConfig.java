package com.choidh.web.common.config;


import com.choidh.web.common.interceptor.CommonInterceptor;
import com.choidh.web.menu.interceptor.MenuInterceptor;
import com.choidh.web.notification.interceptor.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebConfig implements WebMvcConfigurer {
    private final NotificationInterceptor notificationInterceptor;
    private final MenuInterceptor menuInterceptor;
    private final CommonInterceptor commonInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 해당 경로의 파일 요청이 오면 인터셉터 동작 추가.
        List<String> staticResourcesPath = Arrays.stream(StaticResourceLocation.values())
                .flatMap(StaticResourceLocation::getPatterns)
                .collect(Collectors.toList());

        staticResourcesPath.add("/node_modules/**");

        registry.addInterceptor(notificationInterceptor)
                .excludePathPatterns(staticResourcesPath);

        registry.addInterceptor(menuInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(commonInterceptor)
                .addPathPatterns("/**");
    }
}
