package com.choidh.service.mail.service;


import com.choidh.service.mail.vo.EmailCheckMessageVO;
import com.choidh.service.mail.vo.EmailMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 메일 서비스 구현체 (로컬 환경)
 */

@Slf4j
@Service
@Profile(value = {"local"})
public class EmailServiceLocalImpl implements EmailService {
    /**
     * 메일 전송
     */
    @Override
    public void sendEmail(EmailMessageVO emailMessageVO) {
        log.info("sent email: {}", emailMessageVO.getMessage());
    }

    /**
     * 확인용 메일 전송
     */
    @Override
    public void sendCheckEmail(EmailCheckMessageVO emailCheckMessageVO) {
        log.info("sent check email to: {}", emailCheckMessageVO.getNickname());
    }
}
