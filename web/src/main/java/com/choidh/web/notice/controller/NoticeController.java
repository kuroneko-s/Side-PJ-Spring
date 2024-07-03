package com.choidh.web.notice.controller;

import com.choidh.service.notice.service.NoticeService;
import com.choidh.service.notice.vo.NoticeDetailResult;
import com.choidh.service.notice.vo.NoticeListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.choidh.service.common.AppConstant.getTitle;

@Controller
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    /**
     * Get 공지사항 목록 View
     */
    @GetMapping("/list")
    public String getNoticeListView(Model model,
                                    @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        NoticeListResult noticeListResult = noticeService.getNoticeListResultForPublic(pageable);

        model.addAttribute("noticeList", noticeListResult.getNoticeList());
        model.addAttribute(noticeListResult.getPaging());

        model.addAttribute("pageTitle", getTitle("공지사항 목록"));

        return "notice/list/index";
    }

    /**
     * Get 공지사항 View
     */
    @GetMapping("/{noticeId}")
    public String getNoticeDetailView(@PathVariable Long noticeId, Model model) {
        NoticeDetailResult noticeDetail = noticeService.getNoticeDetail(noticeId);

        model.addAttribute("noticeDetail", noticeDetail);
        model.addAttribute("pageTitle", getTitle("공지사항 상세 내용"));

        return "notice/detail/index";
    }
}
