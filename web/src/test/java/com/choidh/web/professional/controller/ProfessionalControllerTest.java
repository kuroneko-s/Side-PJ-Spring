package com.choidh.web.professional.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.account.vo.AccountType;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.attachment.vo.AttachmentFileType;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.learning.service.LearningService;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProfessionalControllerTest extends AbstractControllerTestConfig {
    private final AccountService accountService;
    private final LearningService learningService;
    private final LearningRepository learningRepository;
    private final AttachmentService attachmentService;

    @Test
    @DisplayName("강의 제공자 대시보드 접근 (비로그인)")
    public void getProfessionalDashboardViewNoneLogin() throws Exception {
        mockMvc.perform(get("/professional/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 대시보드 접근 (USER 타입)")
    public void getProfessionalDashboardViewIsUSER() throws Exception {
        mockMvc.perform(get("/professional/dashboard"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 대시보드 접근 (PROFESSIONAL 타입)")
    public void getProfessionalDashboardViewIsPRO() throws Exception {
        mockMvc.perform(get("/professional/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pageTitle", "pageContent"))
                .andExpect(model().attribute("pageContent", "professional/contents"))
                .andExpect(view().name("professional/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 대시보드 접근 (ADMIN 타입)")
    public void getProfessionalDashboardViewIsADMIN() throws Exception {
        mockMvc.perform(get("/professional/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pageTitle", "pageContent"))
                .andExpect(model().attribute("pageContent", "professional/contents"))
                .andExpect(view().name("professional/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 목록 View 접근 (USER 타입)")
    public void getProfessionalLearningListViewIsUSER() throws Exception {
        mockMvc.perform(get("/professional/learning/list"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 목록 View 접근 (PROFESSIONAL 타입)")
    public void getProfessionalLearningListViewIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);

        mockMvc.perform(get("/professional/learning/list")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pageTitle", "pageContent", "learningList", "learningImageMap", "imageUploadMap", "paging"))
                .andExpect(model().attribute("pageContent", "professional/learning/list/contents"))
                .andExpect(view().name("professional/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 목록 View 접근 (ADMIN 타입)")
    public void getProfessionalLearningListViewIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);

        mockMvc.perform(get("/professional/learning/list")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pageTitle", "pageContent", "learningList", "learningImageMap", "imageUploadMap", "paging"))
                .andExpect(model().attribute("pageContent", "professional/learning/list/contents"))
                .andExpect(view().name("professional/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 등록 View 접근 (USER 타입)")
    public void getProfessionalRegLearningViewIsUSER() throws Exception {
        mockMvc.perform(get("/professional/learning/create"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 등록 View 접근 (PROFESSIONAL 타입)")
    public void getProfessionalRegLearningViewIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);

        mockMvc.perform(get("/professional/learning/create"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pageTitle", "pageContent", "whiteList"))
                .andExpect(model().attribute("pageContent", "professional/learning/create/contents"))
                .andExpect(view().name("professional/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 등록 View 접근 (ADMIN 타입)")
    public void getProfessionalRegLearningViewIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);

        mockMvc.perform(get("/professional/learning/create"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pageTitle", "pageContent", "whiteList"))
                .andExpect(model().attribute("pageContent", "professional/learning/create/contents"))
                .andExpect(view().name("professional/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 등록 처리 (USER 타입)")
    public void getProfessionalRegLearningIsUSER() throws Exception {
        mockMvc.perform(get("/professional/learning/create"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 등록 처리 (PROFESSIONAL 타입)")
    public void getProfessionalRegLearningIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        List<Learning> beforeList = learningRepository.findAll();

        mockMvc.perform(
                        post("/professional/learning/create")
                                .param("title", "sample title")
                                .param("price", "1000")
                                .param("subscription", "sample subscription")
                                .param("simpleSubscription", "sample simpleSubscription")
                                .param("mainCategory", "sample mainCategory")
                                .param("skills", "[{\"value\":\"sample\"},{\"value\":\"java\"},{\"value\":\"hello\"}]")
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/professional/learning/video/{\\d+}"));
//                .andExpect(view().name(Matchers.matchesRegex("redirect:/professional/learning/video/\\d+")));
        List<Learning> afterList = learningRepository.findAll();

        assertEquals(beforeList.size() + 1, afterList.size());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 등록 처리 (ADMIN 타입)")
    public void getProfessionalRegLearningIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        List<Learning> beforeList = learningRepository.findAll();

        mockMvc.perform(
                        post("/professional/learning/create")
                                .param("title", "sample title")
                                .param("price", "1000")
                                .param("subscription", "sample subscription")
                                .param("simpleSubscription", "sample simpleSubscription")
                                .param("mainCategory", "sample mainCategory")
                                .param("skills", "[{\"value\":\"sample\"},{\"value\":\"java\"},{\"value\":\"hello\"}]")
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/professional/learning/video/{\\d+}"));
        List<Learning> afterList = learningRepository.findAll();

        assertEquals(beforeList.size() + 1, afterList.size());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 내용 수정 View 접근 (USER 타입)")
    public void getProfessionalModLearningViewIsUSER() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/professional/learning/" + learning.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 내용 수정 View 접근 (PROFESSIONAL 타입)")
    public void getProfessionalModLearningViewIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/professional/learning/" + learning.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("learning", "whiteList", "tagList", "pageTitle", "pageContent"))
                .andExpect(model().attribute("pageContent", "professional/learning/modify/contents"))
                .andExpect(view().name("professional/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 내용 수정 View 접근 (ADMIN 타입)")
    public void getProfessionalModLearningViewIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/professional/learning/" + learning.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("learning", "whiteList", "tagList", "pageTitle", "pageContent"))
                .andExpect(model().attribute("pageContent", "professional/learning/modify/contents"))
                .andExpect(view().name("professional/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 내용 수정 처리 동작 (USER 타입)")
    public void getProfessionalModLearningIsUSER() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(
                post("/professional/learning/" + learning.getId())
                        .param("title", "수정 제목")
                        .param("price", "0")
                        .param("subscription", "수정 설명")
                        .param("simpleSubscription", "수정 간단 설명")
                        .param("mainCategory", "수정 카테고리")
                        .param("skills", "[{\"value\":\"수정\"},{\"value\":\"할거\"},{\"value\":\"에요\"}]")
                        .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 내용 수정 처리 동작 (비정상1) (PROFESSIONAL 타입)")
    public void getProfessionalModLearningFail_1_IsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(
                        post("/professional/learning/" + learning.getId())
                                .param("title", "")
                                .param("price", "0")
                                .param("subscription", "수정 설명")
                                .param("simpleSubscription", "수정 간단 설명")
                                .param("mainCategory", "수정 카테고리")
                                .param("skills", "[{\"value\":\"수정\"},{\"value\":\"할거\"},{\"value\":\"에요\"}]")
                                .with(csrf())
                )
                .andExpect(model().attributeExists("learning", "whiteList", "tagList", "pageTitle", "pageContent", "account"))
                .andExpect(model().attribute("pageContent", "professional/learning/modify/contents"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 내용 수정 처리 동작 (비정상2) (PROFESSIONAL 타입)")
    public void getProfessionalModLearningFail_2_IsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(
                        post("/professional/learning/" + learning.getId())
                                .param("title", "수정 제목")
                                .param("price", "0")
                                .param("subscription", "")
                                .param("simpleSubscription", "수정 간단 설명")
                                .param("mainCategory", "수정 카테고리")
                                .param("skills", "[{\"value\":\"수정\"},{\"value\":\"할거\"},{\"value\":\"에요\"}]")
                                .with(csrf())
                )
                .andExpect(model().attributeExists("learning", "whiteList", "tagList", "pageTitle", "pageContent", "account"))
                .andExpect(model().attribute("pageContent", "professional/learning/modify/contents"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 내용 수정 처리 동작 (비정상3) (PROFESSIONAL 타입)")
    public void getProfessionalModLearningFail_3_IsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(
                        post("/professional/learning/" + learning.getId())
                                .param("title", "수정 제목")
                                .param("price", "0")
                                .param("subscription", "수정 설명")
                                .param("simpleSubscription", "")
                                .param("mainCategory", "수정 카테고리")
                                .param("skills", "[{\"value\":\"수정\"},{\"value\":\"할거\"},{\"value\":\"에요\"}]")
                                .with(csrf())
                )
                .andExpect(model().attributeExists("learning", "whiteList", "tagList", "pageTitle", "pageContent", "account"))
                .andExpect(model().attribute("pageContent", "professional/learning/modify/contents"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 내용 수정 처리 동작 (비정상4) (PROFESSIONAL 타입)")
    public void getProfessionalModLearningFail_4_IsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(
                        post("/professional/learning/" + learning.getId())
                                .param("title", "수정 제목")
                                .param("price", "0")
                                .param("subscription", "수정 설명")
                                .param("simpleSubscription", "수정 간단 설명")
                                .param("mainCategory", "")
                                .param("skills", "[{\"value\":\"수정\"},{\"value\":\"할거\"},{\"value\":\"에요\"}]")
                                .with(csrf())
                )
                .andExpect(model().attributeExists("learning", "whiteList", "tagList", "pageTitle", "pageContent", "account"))
                .andExpect(model().attribute("pageContent", "professional/learning/modify/contents"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 내용 수정 처리 동작 (비정상5) (PROFESSIONAL 타입)")
    public void getProfessionalModLearningFail_5_IsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(
                        post("/professional/learning/" + learning.getId())
                                .param("title", "수정 제목")
                                .param("price", "0")
                                .param("subscription", "수정 설명")
                                .param("simpleSubscription", "수정 간단 설명")
                                .param("mainCategory", "수정 카테고리")
                                .param("skills", "")
                                .with(csrf())
                )
                .andExpect(model().attributeExists("learning", "whiteList", "tagList", "pageTitle", "pageContent", "account"))
                .andExpect(model().attribute("pageContent", "professional/learning/modify/contents"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 내용 수정 처리 동작 (정상) (PROFESSIONAL 타입)")
    public void getProfessionalModLearningSuccessIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning before = createLearning(account);

        this.persistClear();
        mockMvc.perform(
                        post("/professional/learning/" + before.getId())
                                .param("title", "수정 제목")
                                .param("price", "0")
                                .param("subscription", "수정 설명")
                                .param("simpleSubscription", "수정 간단 설명")
                                .param("mainCategory", "수정 카테고리")
                                .param("skills", "[{\"value\":\"수정\"},{\"value\":\"할거\"},{\"value\":\"에요\"}]")
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection());
        this.persistClear();

        Learning after = learningService.getLearningById(before.getId());
        assertNotEquals(before.getTitle(), after.getTitle());
        assertNotEquals(before.getPrice(), after.getPrice());
        assertNotEquals(before.getSubscription(), after.getSubscription());
        assertNotEquals(before.getSimpleSubscription(), after.getSimpleSubscription());
        assertNotEquals(before.getMainCategory(), after.getMainCategory());
        assertNotEquals(before.getTags().size(), after.getTags().size());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 내용 수정 처리 동작 (ADMIN 타입)")
    public void getProfessionalModLearningIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning before = createLearning(account);

        this.persistClear();
        mockMvc.perform(
                        post("/professional/learning/" + before.getId())
                                .param("title", "수정 제목")
                                .param("price", "0")
                                .param("subscription", "수정 설명")
                                .param("simpleSubscription", "수정 간단 설명")
                                .param("mainCategory", "수정 카테고리")
                                .param("skills", "[{\"value\":\"수정\"},{\"value\":\"할거\"},{\"value\":\"에요\"}]")
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection());
        this.persistClear();

        Learning after = learningService.getLearningById(before.getId());
        assertNotEquals(before.getTitle(), after.getTitle());
        assertNotEquals(before.getPrice(), after.getPrice());
        assertNotEquals(before.getSubscription(), after.getSubscription());
        assertNotEquals(before.getSimpleSubscription(), after.getSimpleSubscription());
        assertNotEquals(before.getMainCategory(), after.getMainCategory());
        assertNotEquals(before.getTags().size(), after.getTags().size());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 영상 수정 View (USER 타입)")
    public void getProfessionalModLearningVideoViewIsUSER() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/professional/learning/video/" + learning.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 영상 수정 View (PROFESSIONAL 타입)")
    public void getProfessionalModLearningVideoViewIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/professional/learning/video/" + learning.getId()))
                .andExpect(model().attributeExists("learning", "learningModifyVO", "pageTitle", "pageContent", "account"))
                .andExpect(model().attribute("pageContent", "professional/learning/upload/contents"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 영상 수정 View (ADMIN 타입)")
    public void getProfessionalModLearningVideoViewIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/professional/learning/video/" + learning.getId()))
                .andExpect(model().attributeExists("learning", "learningModifyVO", "pageTitle", "pageContent", "account"))
                .andExpect(model().attribute("pageContent", "professional/learning/upload/contents"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 배너 이미지 수정 (USER 타입)")
    public void getProfessionalModLearningBannerIsUSER() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        MockMultipartFile bannerFile = getMockBannerFile();

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("learningId", String.valueOf(learning.getId()));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/professional/learning/banner/upload")
                        .file(bannerFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 배너 이미지 수정 (PROFESSIONAL 타입)")
    public void getProfessionalModLearningBannerIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        List<AttachmentFile> beforeList = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
        assertTrue(beforeList.isEmpty());

        MockMultipartFile bannerFile = getMockBannerFile();

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("learningId", String.valueOf(learning.getId()));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/professional/learning/banner/upload")
                        .file(bannerFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().isOk());
        this.persistClear();

        List<AttachmentFile> afterList = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
        assertFalse(afterList.isEmpty());
        assertEquals(afterList.size(), 1);
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 배너 이미지 수정 (ADMIN 타입)")
    public void getProfessionalModLearningBannerIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        List<AttachmentFile> beforeList = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
        assertTrue(beforeList.isEmpty());

        MockMultipartFile bannerFile = getMockBannerFile();

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("learningId", String.valueOf(learning.getId()));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/professional/learning/banner/upload")
                        .file(bannerFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().isOk());
        this.persistClear();

        List<AttachmentFile> afterList = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
        assertFalse(afterList.isEmpty());
        assertEquals(afterList.size(), 1);
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 영상 이미지 수정 (USER 타입)")
    public void getProfessionalModLearningVideoIsUSER() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        MockMultipartFile videoFile = getMockVideoFile();

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("learningId", String.valueOf(learning.getId()));
        params.add("title", "새로운 강의 1");
        params.add("order", "1");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/professional/learning/video/upload")
                        .file(videoFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 영상 이미지 수정 (PROFESSIONAL 타입)")
    public void getProfessionalModLearningVideoIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        List<AttachmentFile> beforeList = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);
        assertTrue(beforeList.isEmpty());

        MockMultipartFile videoFile = getMockVideoFile();

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("learningId", String.valueOf(learning.getId()));
        params.add("title", "새로운 강의 1");
        params.add("order", "1");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/professional/learning/video/upload")
                        .file(videoFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().isOk());
        this.persistClear();

        List<AttachmentFile> afterList = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);
        assertFalse(afterList.isEmpty());
        assertEquals(afterList.size(), 1);
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 영상 이미지 수정 (ADMIN 타입)")
    public void getProfessionalModLearningVideoIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);

        List<AttachmentFile> beforeList = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);
        assertTrue(beforeList.isEmpty());

        MockMultipartFile videoFile = getMockVideoFile();

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("learningId", String.valueOf(learning.getId()));
        params.add("title", "새로운 강의 1");
        params.add("order", "1");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/professional/learning/video/upload")
                        .file(videoFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().isOk());
        this.persistClear();

        List<AttachmentFile> afterList = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);
        assertFalse(afterList.isEmpty());
        assertEquals(afterList.size(), 1);
    }



    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("강의 제공자 강의 공개 처리 (USER 타입)")
    public void getProfessionalModLearningOpeningIsUSER() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);
        assertFalse(learning.isOpening());

        mockMvc.perform(
                        post("/professional/learning/opening/" + learning.getId())
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("강의 제공자 강의 공개 처리 (PROFESSIONAL 타입)")
    public void getProfessionalModLearningOpeningIsPRO() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);
        assertFalse(learning.isOpening());

        mockMvc.perform(
                        post("/professional/learning/opening/" + learning.getId())
                                .with(csrf())
                )
                .andExpect(status().isOk());
        this.persistClear();

        Learning after = learningService.getLearningById(learning.getId());
        assertTrue(after.isOpening());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("강의 제공자 강의 공개 처리 (ADMIN 타입)")
    public void getProfessionalModLearningOpeningIsADMIN() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");
        createProfessionalAccount(account);
        Learning learning = createLearning(account);
        assertFalse(learning.isOpening());

        mockMvc.perform(
                        post("/professional/learning/opening/" + learning.getId())
                                .with(csrf())
                )
                .andExpect(status().isOk());
        this.persistClear();

        Learning after = learningService.getLearningById(learning.getId());
        assertTrue(after.isOpening());
    }
}