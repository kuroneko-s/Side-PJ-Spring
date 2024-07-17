package com.choidh.web;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.account.vo.RegAccountVO;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.service.AttachmentServiceImpl;
import com.choidh.service.attachment.vo.AttachmentFileType;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.common.exception.FileNotSavedException;
import com.choidh.service.common.utiles.FileUtils;
import com.choidh.service.common.utiles.StringUtils;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.learning.vo.RegLearningVO;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.professional.service.ProfessionalService;
import com.choidh.service.professional.vo.RegProfessionalAccountVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.*;
import java.nio.file.Path;

@Slf4j
@ActiveProfiles("local")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class AbstractControllerTestConfig {
    @Autowired
    protected EntityManager entityManager;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected CartService cartService;
    @Autowired
    protected AccountService accountService;
    @Autowired
    protected ProfessionalService professionalService;
    @Autowired
    protected LearningService learningService;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    protected Account createAccount() {
        return this.createAccount("테스트냥이", "test@test.com");
    }

    /**
     * 샘플 계정 생성
     */
    protected Account createAccount(String nickname, String email) {
        return accountService.regAccount(RegAccountVO.builder()
                .nickname(nickname)
                .email(email)
                .password("1234567890")
                .passwordcheck("1234567890")
                .build());
    }

    /**
     * 샘플 강의 제공자 생성
     */
    protected ProfessionalAccount createProfessionalAccount(Account account) {
        return professionalService.regProfessionalAccount(RegProfessionalAccountVO.builder()
                .account(account)
                .name("강사 이름")
                .description("개잘나가는 강사임")
                .history("개쩌는 경력들")
                .build());
    }

    /**
     * 샘플 강의 생성
     */
    protected Learning createLearning(Account account) {
        return learningService.regLearning(RegLearningVO.builder()
                        .title("샘플 강의 1")
                        .simpleSubscription("간단한 강의 설명")
                        .subscription("장황한 강의 설명글")
                        .price(10000)
                        .mainCategory("자바")
                        .skills("[{\"value\":\"sample\"},{\"value\":\"java\"},{\"value\":\"hello\"}]")
                        .build(),
                account.getId());
    }

    protected void persistClear() {
        entityManager.flush();
        entityManager.clear();
    }

    protected void theLine() {
        persistClear();

        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
    }

    protected static MockMultipartFile getMockContextFile() {
        return new MockMultipartFile(
                "context",                   // 파라미터 이름
                "context.txt",               // 파일 이름
                "text/plain",                // 파일 타입
                "Context content".getBytes() // 파일 내용
        );
    }

    protected static MockMultipartFile getMockBannerFile() {
        return new MockMultipartFile(
                "banner",                   // 파라미터 이름
                "banner.jpg",               // 파일 이름
                "image/jpeg",               // 파일 타입
                "Banner content".getBytes() // 파일 내용
        );
    }

    protected static MockMultipartFile getMockVideoFile() {
        return new MockMultipartFile(
                "video",                   // 파라미터 이름
                "video.jpg",               // 파일 이름
                "video/mp4",               // 파일 타입
                "Banner content".getBytes() // 파일 내용
        );
    }
}
