package com.choidh.service.notification.vo;

/**
 * 알람 종류
 */

public enum  NotificationType {
    LEARNING_CREATE, // 강의 생성
    LEARNING_UPDATE, // 강의 수정
    LEARNING_CLOSE, // 강의 종료
    NOTICE, // 강의 공지사항
    SITE, // 사이트 관련
    EVENT, // 이벤트 관련
}
