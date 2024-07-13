package com.choidh.web.admin.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.AccountType;
import com.choidh.service.event.entity.Event;
import com.choidh.service.event.service.EventService;
import com.choidh.service.notice.entity.Notice;
import com.choidh.service.notice.service.NoticeService;
import com.choidh.service.notice.vo.NoticeListResult;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
class AdminControllerTest extends AbstractControllerTestConfig {
    @Autowired
    private EventService eventService;
    @Autowired
    private NoticeService noticeService;

    @Test
    @DisplayName("관리자 대시보드 접근 (비로그인)")
    public void getAdminDashboardViewNoneLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 대시보드 접근 (USER 타입)")
    public void getAdminDashboardViewIsUSER() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 대시보드 접근 (PROFESSIONAL 타입)")
    public void getAdminDashboardViewIsPRO() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 대시보드 접근 (ADMIN 타입)")
    public void getAdminDashboardViewIsADMIN() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 이벤트 리스트 접근 (USER 타입)")
    public void getAdminEventListViewIsUSER() throws Exception {
        mockMvc.perform(get("/admin/event/list"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 이벤트 리스트 접근 (PROFESSIONAL 타입)")
    public void getAdminEventListViewIsPRO() throws Exception {
        mockMvc.perform(get("/admin/event/list"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 이벤트 리스트 접근 (ADMIN 타입)")
    public void getAdminEventListViewIsADMIN() throws Exception {
        mockMvc.perform(get("/admin/event/list"))
                .andExpect(model().attributeExists("eventList"))
                .andExpect(model().attributeExists("imageMap"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 이벤트 등록 페이지 접근 (USER 타입)")
    public void getAdminRegEventViewIsUSER() throws Exception {
        mockMvc.perform(get("/admin/event/create"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 이벤트 등록 페이지 접근 (PROFESSIONAL 타입)")
    public void getAdminRegEventViewIsPRO() throws Exception {
        mockMvc.perform(get("/admin/event/create"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 이벤트 등록 페이지 접근 (ADMIN 타입)")
    public void getAdminRegEventViewIsADMIN() throws Exception {
        mockMvc.perform(get("/admin/event/create"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 이벤트 등록 (PROFESSIONAL 타입)")
    public void getAdminRegEventIsPRO() throws Exception {
        MockMultipartFile bannerFile = getMockBannerFile();

        MockMultipartFile contextFile = getMockContextFile();

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "Sample Event");
        params.add("description", "Sample Event Description");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/event/create")
                        .file(bannerFile)
                        .file(contextFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 이벤트 등록 (ADMIN 타입)")
    public void getAdminRegEventIsADMIN() throws Exception {
        MockMultipartFile bannerFile = getMockBannerFile();

        MockMultipartFile contextFile = getMockContextFile();

        List<Event> beforeEventList = eventService.getEventList();

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "Sample Event");
        params.add("description", "Sample Event Description");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/event/create")
                        .file(bannerFile)
                        .file(contextFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        List<Event> afterEventList = eventService.getEventList();

        assertNotEquals(beforeEventList.size(), afterEventList.size());
        assertEquals(beforeEventList.size() + 1, afterEventList.size());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 이벤트 수정 페이지 접근 (PROFESSIONAL 타입)")
    public void getAdminModEventViewIsPRO() throws Exception {
        List<Event> eventList = eventService.getEventList();
        Event event = eventList.get(0);

        mockMvc.perform(get("/admin/event/" + event.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 이벤트 수정 페이지 접근 (ADMIN 타입)")
    public void getAdminModEventViewIsUSER() throws Exception {
        List<Event> eventList = eventService.getEventList();
        Event event = eventList.get(0);

        mockMvc.perform(get("/admin/event/" + event.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("eventDetail"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 이벤트 수정 동작 (PROFESSIONAL 타입)")
    public void getAdminModEventIsPRO() throws Exception {
        MockMultipartFile bannerFile = getMockBannerFile();
        MockMultipartFile contextFile = getMockContextFile();

        List<Event> beforeEventList = eventService.getEventList();
        Event beforeEvent = beforeEventList.get(0);

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("eventId", beforeEvent.getId().toString());
        params.add("title", "이벤트 제목 수정");
        params.add("description", "이벤트 내용 수정");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/event/modify")
                        .file(bannerFile)
                        .file(contextFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 이벤트 수정 동작 (ADMIN 타입)")
    public void getAdminModEventIsADMIN() throws Exception {
        MockMultipartFile bannerFile = getMockBannerFile();
        MockMultipartFile contextFile = getMockContextFile();

        List<Event> beforeEventList = eventService.getEventList();
        Event beforeEvent = beforeEventList.get(0);

        // EventFormVO를 설정하기 위한 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("eventId", beforeEvent.getId().toString());
        params.add("title", "이벤트 제목 수정");
        params.add("description", "이벤트 내용 수정");

        this.persistClear();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/event/modify")
                        .file(bannerFile)
                        .file(contextFile)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        List<Event> afterEventList = eventService.getEventList();
        Event afterEvent = afterEventList.get(0);

        assertNotEquals(beforeEvent.getTitle(), afterEvent.getTitle());
        assertNotEquals(beforeEvent.getDescription(), afterEvent.getDescription());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 이벤트 삭제 동작 (PROFESSIONAL 타입)")
    public void getAdminDelEventIsPRO() throws Exception {
        List<Event> beforeEventList = eventService.getEventList();
        Event event = beforeEventList.get(0);

        mockMvc.perform(delete("/admin/event/" + event.getId())
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 이벤트 삭제 동작 (ADMIN 타입)")
    public void getAdminDelEventIsADMIN() throws Exception {
        List<Event> beforeEventList = eventService.getEventList();
        Event event = beforeEventList.get(0);

        mockMvc.perform(
                        delete("/admin/event/" + event.getId())
                                .with(csrf()))
                .andExpect(status().isOk());

        this.persistClear();

        Event afterEvent = eventService.getEventById(event.getId());
        assertFalse(afterEvent.isUsed());
    }

    // ========= 이벤트 관련 기능 종료 =========

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 공지사항 목록 화면 접근 (USER 타입)")
    public void getAdminNoticeListViewIsUSER() throws Exception {
        mockMvc.perform(get("/admin/notice/list"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 공지사항 목록 화면 접근 (PROFESSIONAL 타입)")
    public void getAdminNoticeListViewIsPRO() throws Exception {
        mockMvc.perform(get("/admin/notice/list"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 공지사항 목록 화면 접근 (ADMIN 타입)")
    public void getAdminNoticeListViewIsADMIN() throws Exception {
        mockMvc.perform(get("/admin/notice/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createAt,desc"))
                .andExpect(model().attributeExists("noticeList"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 공지사항 등록 화면 접근 (USER 타입)")
    public void getAdminRedNoticeViewIsUSER() throws Exception {
        mockMvc.perform(get("/admin/notice/create"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 공지사항 등록 화면 접근 (PROFESSIONAL 타입)")
    public void getAdminRedNoticeViewIsPRO() throws Exception {
        mockMvc.perform(get("/admin/notice/create"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 공지사항 등록 화면 접근 (ADMIN 타입)")
    public void getAdminRedNoticeViewIsADMIN() throws Exception {
        mockMvc.perform(get("/admin/notice/create"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 공지사항 등록 처리 (USER 타입)")
    public void getAdminRedNoticeIsUSER() throws Exception {
        mockMvc.perform(post("/admin/notice/create")
                        .param("title", "샘플 공지사항")
                        .param("content", "샘플 내용")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 공지사항 등록 처리 (PROFESSIONAL 타입)")
    public void getAdminRedNoticeIsPRO() throws Exception {
        mockMvc.perform(post("/admin/notice/create")
                        .param("title", "샘플 공지사항")
                        .param("content", "샘플 내용")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 공지사항 등록 처리 (ADMIN 타입)")
    public void getAdminRedNoticeIsADMIN() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));

        mockMvc.perform(post("/admin/notice/create")
                        .param("title", "샘플 공지사항")
                        .param("content", "샘플 내용")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        this.persistClear();

        NoticeListResult afterList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));

        assertEquals(beforeList.getNoticeList().size() + 1, afterList.getNoticeList().size());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 공지사항 수정 화면 접근 (USER 타입)")
    public void getAdminModNoticeViewIsUSER() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        mockMvc.perform(get("/admin/notice/" + notice.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 공지사항 수정 화면 접근 (PROFESSIONAL 타입)")
    public void getAdminModNoticeViewIsPRO() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        mockMvc.perform(get("/admin/notice/" + notice.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 공지사항 수정 화면 접근 (ADMIN 타입)")
    public void getAdminModNoticeViewIsADMIN() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        mockMvc.perform(get("/admin/notice/" + notice.getId()))
                .andExpect(model().attributeExists("noticeDetail"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 공지사항 수정 동작 (USER 타입)")
    public void getAdminModNoticeIsUSER() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        mockMvc.perform(
                        post("/admin/notice/modify")
                                .param("noticeId", notice.getId().toString())
                                .param("title", "수정 제목")
                                .param("content", "수정 내용")
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 공지사항 수정 동작 (PROFESSIONAL 타입)")
    public void getAdminModNoticeIsPRO() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        mockMvc.perform(
                        post("/admin/notice/modify")
                                .param("noticeId", notice.getId().toString())
                                .param("title", "수정 제목")
                                .param("content", "수정 내용")
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 공지사항 수정 동작 (ADMIN 타입)")
    public void getAdminModNoticeIsADMIN() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        this.persistClear();
        mockMvc.perform(
                        post("/admin/notice/modify")
                                .param("noticeId", notice.getId().toString())
                                .param("title", "수정 제목")
                                .param("content", "수정 내용")
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection());

        Notice afterNotice = noticeService.getNoticeById(notice.getId());
        assertEquals(notice.getId(), afterNotice.getId());
        assertNotEquals(notice.getTitle(), afterNotice.getTitle());
        assertNotEquals(notice.getContent(), afterNotice.getContent());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 공지사항 삭제 동작 (USER 타입)")
    public void getAdminDelNoticeIsUSER() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        mockMvc.perform(delete("/admin/notice/" + notice.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 공지사항 삭제 동작 (PROFESSIONAL 타입)")
    public void getAdminDelNoticeIsPRO() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        mockMvc.perform(delete("/admin/notice/" + notice.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 공지사항 삭제 동작 (ADMIN 타입)")
    public void getAdminDelNoticeIsADMIN() throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));
        Notice notice = beforeList.getNoticeList().get(0);

        mockMvc.perform(delete("/admin/notice/" + notice.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        this.persistClear();

        assertThrows(IllegalArgumentException.class, () -> {
            noticeService.getNoticeById(notice.getId());
        });
    }

    // ========= 공지사항 관련 기능 종료 =========

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 강의 제공자 목록 화면 접근 (USER 타입)")
    public void getAdminProfessionalListViewIsUSER() throws Exception {
        mockMvc.perform(get("/admin/professional/list"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 강의 제공자 목록 화면 접근 (PROFESSIONAL 타입)")
    public void getAdminProfessionalListViewIsPRO() throws Exception {
        mockMvc.perform(get("/admin/professional/list"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 강의 제공자 목록 화면 접근 (ADMIN 타입)")
    public void getAdminProfessionalListViewIsADMIN() throws Exception {
        mockMvc.perform(
                        get("/admin/professional/list")
                                .param("page", "0")
                )
                .andExpect(model().attributeExists("professionalAccountList"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 강의 제공자 활성화 동작 (USER 타입)")
    public void getAdminModProfessionalIsUSER() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount("testAmple", "testSample@sample.com"));
        assertFalse(professionalAccount.isUsed());

        this.persistClear();

        mockMvc.perform(
                post("/admin/professional/" + professionalAccount.getId())
                        .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 강의 제공자 활성화 동작 (PROFESSIONAL 타입)")
    public void getAdminModProfessionalIsPRO() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount("testAmple", "testSample@sample.com"));
        assertFalse(professionalAccount.isUsed());

        this.persistClear();

        mockMvc.perform(
                        post("/admin/professional/" + professionalAccount.getId())
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 강의 제공자 활성화 동작 (ADMIN 타입)")
    public void getAdminModProfessionalIsADMIN() throws Exception {
        Account account = createAccount("testAmple", "testSample@sample.com");
        ProfessionalAccount before = createProfessionalAccount(account);
        assertFalse(before.isUsed());

        this.persistClear();

        mockMvc.perform(
                        post("/admin/professional/" + before.getId())
                                .with(csrf())
                )
                .andExpect(status().isOk());

        ProfessionalAccount after = professionalService.getProfessionalByAccountId(account.getId());
        assertTrue(after.isUsed());
    }


    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.USER)
    @DisplayName("관리자 강의 제공자 비활성화 동작 (USER 타입)")
    public void getAdminDelProfessionalIsUSER() throws Exception {
        Account account = createAccount("testAmple", "testSample@sample.com");
        professionalService.modProfessionalById(createProfessionalAccount(account).getId());
        this.persistClear();

        ProfessionalAccount before = professionalService.getProfessionalByAccountId(account.getId());
        assertTrue(before.isUsed());

        this.persistClear();
        mockMvc.perform(
                        delete("/admin/professional/" + before.getId())
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.PROFESSIONAL)
    @DisplayName("관리자 강의 제공자 비활성화 동작 (PROFESSIONAL 타입)")
    public void getAdminDelProfessionalIsPRO() throws Exception {
        Account account = createAccount("testAmple", "testSample@sample.com");
        professionalService.modProfessionalById(createProfessionalAccount(account).getId());
        this.persistClear();

        ProfessionalAccount before = professionalService.getProfessionalByAccountId(account.getId());
        assertTrue(before.isUsed());

        this.persistClear();
        mockMvc.perform(
                        delete("/admin/professional/" + before.getId())
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAccount(value = "test@test.com", accountType = AccountType.ADMIN)
    @DisplayName("관리자 강의 제공자 비활성화 동작 (ADMIN 타입)")
    public void getAdminDelProfessionalIsADMIN() throws Exception {
        Account account = createAccount("testAmple", "testSample@sample.com");
        professionalService.modProfessionalById(createProfessionalAccount(account).getId());
        this.persistClear();

        ProfessionalAccount before = professionalService.getProfessionalByAccountId(account.getId());
        assertTrue(before.isUsed());

        this.persistClear();
        mockMvc.perform(
                        delete("/admin/professional/" + before.getId())
                                .with(csrf())
                )
                .andExpect(status().isOk());

        ProfessionalAccount after = professionalService.getProfessionalByAccountId(account.getId());
        assertFalse(after.isUsed());
    }
}