package com.choidh.web.notice.controller;

import com.choidh.service.notice.entity.Notice;
import com.choidh.service.notice.service.NoticeService;
import com.choidh.service.notice.vo.NoticeListResult;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
class NoticeControllerTest extends AbstractControllerTestConfig {
    @Autowired
    private NoticeService noticeService;

    @Test
    @DisplayName("Get 공지사항 목록 View (비로그인)")
    public void getNoticeListViewNoneLogin() throws Exception {
        mockMvc.perform(get("/notice/list")
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Get 공지사항 목록 View (로그인)")
    public void getNoticeListViewLogin() throws Exception {
        mockMvc.perform(get("/notice/list")
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(model().attributeExists("noticeList", "paging", "pageTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/list/index"));
    }

    @Test
    @DisplayName("Get 공지사항 상세 조회 View (비로그인)")
    public void getNoticeDetailViewNoneLogin() throws Exception {
        NoticeListResult noticeListResult = noticeService.getNoticeListResultForPublic(PageRequest.of(0, 10));
        List<Notice> noticeList = noticeListResult.getNoticeList();
        assertFalse(noticeList.isEmpty());
        Notice notice = noticeList.get(0);

        mockMvc.perform(get("/notice/" + notice.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("Get 공지사항 상세 조회 View (로그인)")
    public void getNoticeDetailViewLogin() throws Exception {
        NoticeListResult noticeListResult = noticeService.getNoticeListResultForPublic(PageRequest.of(0, 10));
        List<Notice> noticeList = noticeListResult.getNoticeList();
        assertFalse(noticeList.isEmpty());
        Notice notice = noticeList.get(0);

        mockMvc.perform(get("/notice/" + notice.getId()))
                .andExpect(model().attributeExists("noticeDetail", "pageTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/detail/index"));
    }
}