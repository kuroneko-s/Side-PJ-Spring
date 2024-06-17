package com.choidh.web.common.config;

import org.springframework.stereotype.Component;

@Component
public class WebSecurity {
    public boolean checkLearningId(String id) {
        try {
            Integer.parseInt(id); // id가 Integer로 변환 가능한지 확인
            return true; // 변환 가능하면 true 반환
        } catch (NumberFormatException e) {
            return false; // 변환 불가능하면 false 반환
        }
    }
}
