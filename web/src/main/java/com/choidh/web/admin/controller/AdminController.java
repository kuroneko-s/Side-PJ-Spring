package com.choidh.web.admin.controller;

import com.choidh.service.event.service.EventService;
import com.choidh.service.event.vo.EventListResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.choidh.service.common.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired private EventService eventService;

    /**
     * Get 관리자 대시보드 View
     */
    @GetMapping("/dashboard")
    public String getAdminDashboardView(Model model) {
        model.addAttribute("pageTitle", getTitle("관리자 대시보드"));
        model.addAttribute("pageContent", "admin/contents");

        return "admin/index";
    }

    /**
     * Get 이벤트 목록 View
     */
    @GetMapping("/event/list")
    public String getEventListView(Model model, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        EventListResult eventListResult = eventService.getEventListResult(pageable);

        model.addAttribute("eventList", eventListResult.getEventList());
        model.addAttribute("imageMap", eventListResult.getImageMap());
        model.addAttribute(eventListResult.getPaging());

        model.addAttribute("pageTitle", getTitle("이벤트 목록"));
        model.addAttribute("pageContent", "admin/event/list/contents");

        return "admin/index";
    }

    /**
     * Get 이벤트 등록 View
     */
    @GetMapping("/event/create")
    public String getRegEventView(Model model) {
        model.addAttribute("pageTitle", getTitle("이벤트 등록"));
        model.addAttribute("pageContent", "admin/event/create/contents");

        return "admin/index";
    }

    /**
     * Get 이벤트 수정 View
     */
    @GetMapping("/event/{eventId}")
    public String getEventModifyView(Model model, @PathVariable Long eventId) {
        model.addAttribute("pageTitle", getTitle("이벤트 등록"));
        model.addAttribute("pageContent", "admin/event/modify/contents");

        return "admin/index";
    }

    /**
     * Patch 이벤트 수정
     */
    @PatchMapping("/event/{eventId}")
    public String modEvent(Model model, @PathVariable Long eventId) {
        return "redirect:/event/" + eventId;
    }

    /**
     * Del 이벤트 삭제
     */
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity delEvent(@PathVariable Long eventId) {
        // 삭제 동작.

        return ResponseEntity.ok("이벤트가 삭제되었어요!");
    }

    /**
     * Get 공지사항 목록 View
     */
    @GetMapping("/notice/list")
    public String getNoticeListView(Model model) {
        model.addAttribute("pageTitle", getTitle("공지사항 목록"));
        model.addAttribute("pageContent", "admin/notice/list/contents");

        return "admin/index";
    }

    /**
     * Get 공지사항 등록 View
     */
    @GetMapping("/notice/create")
    public String getRegNoticeView(Model model) {
        model.addAttribute("pageTitle", getTitle("공지사항 등록"));
        model.addAttribute("pageContent", "admin/notice/create/contents");

        return "admin/index";
    }

    /**
     * Get 강의 제공자 목록 View
     */
    @GetMapping("/professional/list")
    public String getProfessionalListView(Model model) {
        // 이화면에서 등록 및 해제가 가능하도록.
        model.addAttribute("pageTitle", getTitle("강의 제공자 목록"));
        model.addAttribute("pageContent", "admin/professional/list/contents");

        return "admin/index";
    }
}
