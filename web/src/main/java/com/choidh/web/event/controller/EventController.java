package com.choidh.web.event.controller;

import com.choidh.service.event.service.EventService;
import com.choidh.service.event.vo.EventDetailResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    /**
     * Get 이벤트 상세 조회 View
     */
    @GetMapping("/{eventId}")
    public String getEventDetailView(Model model, @PathVariable Long eventId) {
        // 이벤트 정보 조회.
        EventDetailResult eventDetail = eventService.getEventDetail(eventId);

        model.addAttribute("eventDetail", eventDetail);
        model.addAttribute("pageTitle", getTitle(eventDetail.getTitle() + " 이벤트"));

        return "event/detail/index";
    }
}
