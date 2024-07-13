package com.choidh.web;

import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
class MainControllerTest extends AbstractControllerTestConfig {
    @Test
    @DisplayName("/ - 호출 (비로그인)")
    public void home_NoneLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(model().attributeExists("eventFileList"))
                .andExpect(model().attributeExists("learningList"))
                .andExpect(model().attributeExists("learningImageMap"))
                .andExpect(model().attributeExists("newLearningList"))
                .andExpect(model().attributeExists("newLearningImageMap"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeDoesNotExist("account"))
                .andExpect(view().name("home/index"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("user1@user.com")
    @DisplayName("/ - 호출 (로그인)")
    public void home_WithLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(model().attributeExists("eventFileList"))
                .andExpect(model().attributeExists("learningList"))
                .andExpect(model().attributeExists("learningImageMap"))
                .andExpect(model().attributeExists("newLearningList"))
                .andExpect(model().attributeExists("newLearningImageMap"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("home/index"))
                .andExpect(status().isOk());
    }
}