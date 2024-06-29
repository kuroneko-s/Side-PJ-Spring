package com.choidh.web.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.choidh.service.common.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

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
    public String getEventListView(Model model) {
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
