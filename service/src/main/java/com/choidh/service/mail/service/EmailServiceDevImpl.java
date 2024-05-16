package com.choidh.service.mail.service;


import com.choidh.service.common.ServiceAppProperties;
import com.choidh.service.mail.vo.EmailCheckMessageVO;
import com.choidh.service.mail.vo.EmailMessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 메일 서비스 구현체 (실제 동작)
 */

@Slf4j
@Profile(value = {"dev"})
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailServiceDevImpl implements EmailService {
    private final JavaMailSender javaMailSender = new JavaMailSenderImpl();
    private final ServiceAppProperties serviceAppProperties;
    private final TemplateEngine templateEngine;

    /**
     * 메일 전송
     */
    @Override
    public void sendEmail(EmailMessageVO emailMessageVO) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8"); //첨부파일시 true
            mimeMessageHelper.setTo(emailMessageVO.getTo());
            mimeMessageHelper.setSubject(emailMessageVO.getSubject());
            mimeMessageHelper.setText(emailMessageVO.getMessage(), true);
            mimeMessageHelper.setFrom("kuronekospi@gmail.com");
            javaMailSender.send(mimeMessage);

            log.info("sent email: {}", emailMessageVO.getMessage());
        } catch (MessagingException e) {
            log.error("failed to send email", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 확인용 메일 전송
     */
    @Override
    public void sendCheckEmail(EmailCheckMessageVO emailCheckMessageVO) {
        Context context = new Context();
        context.setVariable("nickname", emailCheckMessageVO.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "이메일 인증을 마치시려면 링크를 클릭해주세요.");
        context.setVariable("host", serviceAppProperties.getHost());
        context.setVariable("link", "/mailAuth?token=" + emailCheckMessageVO.getEmailCheckToken() + "&email=" + emailCheckMessageVO.getEmail());

        this.sendEmail(EmailMessageVO.builder()
                .to(emailCheckMessageVO.getEmail())
                .subject("회원 가입 안내 메일")
                .message(templateEngine.process("mail/simplemail", context))
                .build()
        );

        log.info("/mailAuth?token={}&email={}", emailCheckMessageVO.getEmailCheckToken(), emailCheckMessageVO.getEmail());
    }
}
