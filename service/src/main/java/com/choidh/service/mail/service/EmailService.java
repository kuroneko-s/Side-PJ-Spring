package com.choidh.service.mail.service;


import com.choidh.service.mail.vo.EmailForAuthenticationVO;
import com.choidh.service.mail.vo.EmailMessageVO;

/**
 * 메일 서비스
 */

public interface EmailService {
    /**
     * 메일 전송
     */
    void sendEmail(EmailMessageVO emailMessageVO);

    /**
     * 확인용 메일 전송
     */
    void sendEmailForAuthentication(EmailForAuthenticationVO emailForAuthenticationVO);
}
