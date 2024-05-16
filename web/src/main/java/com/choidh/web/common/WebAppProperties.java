package com.choidh.web.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 공통 기능. properties에서 설정 가능.
 */

@Component
@Getter @Setter
@ConfigurationProperties("app.web")
public class WebAppProperties {
    private String host;
}
