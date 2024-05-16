package com.choidh.web.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Validator 유형임을 나타내는 마킹용 애노테이션
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface IsValidator {
}
