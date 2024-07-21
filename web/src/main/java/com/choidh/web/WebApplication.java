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

/**
 * 이전 프로젝트 내용 24-05-16
 *  EnableJdbcHttpSession를 사용하라고 나왔는데 동작안함. 그래서 springhttpsession 사용하고있음.
 *  Session 의 역할을 제대로 알고 적당한 위치에서 사용을 해줘야할듯? 우선 테이블 생성되는 코드로 변경
 *  Spring-Session이 여러 클라이언트 간 데이터를 공유해서 사용하기 위함임.
 *  @EnableSpringHttpSession
 */

@SpringBootApplication(scanBasePackageClasses = {ServiceApplication.class, WebApplication.class})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
