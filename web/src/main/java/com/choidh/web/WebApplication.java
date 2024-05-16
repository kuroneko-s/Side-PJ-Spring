package com.choidh.web;

import com.choidh.service.ServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * scanBase 를 추가해서 적용하는 방식을 채용하고 있는데 이렇게하면 모듈을 나눈 이유가 희미.....해지는듯??
 * 스프링부트 자동 설정처럼 없으면 등록시키고 이런 유동적으로 돌리던지
 * 아니면 웹 부분과 서비스 부분을 완전히 분리시키고 이를 연결시키는 중간 어댑터 기능을만을 하는 부분을 추가해서 그 부분의 빈정도만
 * 자동으로 등록하던가해서 의존 분산 해줘야할 듯?
 */

@SpringBootApplication(scanBasePackageClasses = {ServiceApplication.class, WebApplication.class})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
