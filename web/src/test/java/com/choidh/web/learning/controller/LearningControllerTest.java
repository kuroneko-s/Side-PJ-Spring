package com.choidh.web.learning.controller;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.attachment.vo.AttachmentFileType;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
class LearningControllerTest extends AbstractControllerTestConfig {
    @Autowired
    private LearningService learningService;
    @Autowired
    private AttachmentService attachmentService;

    @Test
    @DisplayName("Get 강의 목록 View 접근 (비로그인)")
    public void getLearningListViewNoneLogin() throws Exception {
        mockMvc.perform(get("/learning/search/all")
                        .param("keyword", "sample")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("mainCategory", "subCategory", "learningList", "learningImageMap", "pageTitle", "paging"))
                .andExpect(view().name("learning/list/index"));
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Get 강의 목록 View 접근 (로그인)")
    public void getLearningListViewLogin() throws Exception {
        mockMvc.perform(get("/learning/search/all")
                        .param("keyword", "sample")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("mainCategory", "subCategory", "learningList", "learningImageMap", "pageTitle", "paging"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("learning/list/index"));
    }

    @Test
    @DisplayName("Post 강의 목록 API (비로그인)")
    public void getLearningListAPINoneLogin() throws Exception {
        String keyword = "sample";

        mockMvc.perform(post("/learning/search/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(keyword)
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Post 강의 목록 API (로그인)")
    public void getLearningListAPILogin() throws Exception {
        String keyword = "sample";

        mockMvc.perform(post("/learning/search/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(keyword)
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get 강의 상세 View (비로그인)")
    public void getLearningDetailViewNoneLogin() throws Exception {
        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        Learning learning = learningList.get(0);

        mockMvc.perform(get("/learning/" + learning.getId()))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("professionalAccount"))
                .andExpect(model().attributeExists("questions"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attributeExists("bannerImage"))
                .andExpect(model().attributeExists("videoFileList"))
                .andExpect(model().attributeExists("nowListening"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("learning/detail/index"));
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Get 강의 상세 View (로그인)")
    public void getLearningDetailViewLogin() throws Exception {
        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        Learning learning = learningList.get(0);

        mockMvc.perform(get("/learning/" + learning.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("professionalAccount"))
                .andExpect(model().attributeExists("questions"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attributeExists("bannerImage"))
                .andExpect(model().attributeExists("videoFileList"))
                .andExpect(model().attributeExists("nowListening"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("learning/detail/index"));
    }

    @Test
    @DisplayName("Get 강의 학습 View (비로그인)")
    public void getLearningListenViewNoneLogin() throws Exception {
        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        Learning learning = learningList.get(0);

        mockMvc.perform(get("/learning/listen/" + learning.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Get 강의 학습 View (로그인)")
    public void getLearningListenViewLogin() throws Exception {
        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        Learning learning = learningList.get(0);

        mockMvc.perform(get("/learning/listen/" + learning.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("titleSet"))
                .andExpect(model().attributeExists("videoSrc"))
                .andExpect(model().attributeExists("videoFileIdMap"))
                .andExpect(model().attributeExists("playingVideo"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("learning/listen/index"));
    }

    @Test
    @DisplayName("Post 강의 경로. API (비로그인)")
    public void postLearningPathAPINoneLogin() throws Exception {
        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        Learning learning = learningList.get(0);

        mockMvc.perform(
                        post("/learning/listen/" + learning.getId())
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Post 강의 경로. API (로그인)")
    public void postLearningPathAPILogin() throws Exception {
        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        Learning learning = learningList.get(0);

        AttachmentGroup attachmentGroup = learning.getAttachmentGroup();
        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.VIDEO);
        assertFalse(attachmentFiles.isEmpty());
        AttachmentFile attachmentFile = attachmentFiles.get(0);

        mockMvc.perform(
                        post("/learning/listen/" + learning.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.valueOf(attachmentFile.getId()))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().string(attachmentFile.getFullPath("")));
    }
}