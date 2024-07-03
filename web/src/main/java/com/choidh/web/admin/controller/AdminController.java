package com.choidh.web.admin.controller;

import com.choidh.service.event.entity.Event;
import com.choidh.service.event.service.EventService;
import com.choidh.service.event.vo.EventDetailResult;
import com.choidh.service.event.vo.EventListResult;
import com.choidh.service.event.vo.EventVO;
import com.choidh.service.notice.service.NoticeService;
import com.choidh.service.notice.vo.NoticeDetailResult;
import com.choidh.service.notice.vo.NoticeListResult;
import com.choidh.service.notice.vo.NoticeVO;
import com.choidh.service.professional.service.ProfessionalService;
import com.choidh.service.professional.vo.ProfessionalListResult;
import com.choidh.web.admin.vo.EventFormVO;
import com.choidh.web.notice.vo.NoticeFormVO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static com.choidh.service.common.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    private EventService eventService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ProfessionalService professionalService;

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
     * Post 이벤트 등록
     */
    @PostMapping("/event/create")
    public String postRegEvent(EventFormVO eventVO,
                               @RequestParam("banner") MultipartFile bannerFile,
                               @RequestParam("context") MultipartFile contextFile) {
        Map<String, MultipartFile> multipartFileMap = new HashMap<>();
        multipartFileMap.put("banner", bannerFile);
        multipartFileMap.put("context", contextFile);

        // 이벤트 등록 처리.
        eventService.regEvent(modelMapper.map(eventVO, EventVO.class), multipartFileMap);

        return "redirect:/admin/event/list";
    }

    /**
     * Get 이벤트 수정 View
     */
    @GetMapping("/event/{eventId}")
    public String getEventModifyView(Model model, @PathVariable Long eventId) {
        // 이벤트 정보 조회.
        EventDetailResult eventDetail = eventService.getEventDetail(eventId);

        model.addAttribute("eventDetail", eventDetail);
        model.addAttribute("pageTitle", getTitle(eventDetail.getTitle() + " 수정"));
        model.addAttribute("pageContent", "admin/event/modify/contents");

        return "admin/index";
    }

    /**
     * Post 이벤트 수정
     */
    @PostMapping("/event/modify")
    public String modEvent(EventFormVO eventVO,
                           @RequestParam(name = "banner", required = false) MultipartFile bannerFile,
                           @RequestParam(name = "context", required = false) MultipartFile contextFile) {
        // 이벤트 수정 동작.
        Map<String, MultipartFile> multipartFileMap = new HashMap<>();
        if (bannerFile != null && !bannerFile.isEmpty()) {
            multipartFileMap.put("banner", bannerFile);
        }

        if (contextFile != null && !contextFile.isEmpty()) {
            multipartFileMap.put("context", contextFile);
        }

        // 이벤트 수정 처리.
        Event event = eventService.modEvent(modelMapper.map(eventVO, EventVO.class), multipartFileMap);

        return "redirect:/event/" + event.getId();
    }

    /**
     * Del 이벤트 삭제
     */
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity delEvent(@PathVariable Long eventId) {
        // 삭제 동작.
        eventService.delEvent(eventId);

        return ResponseEntity.ok("이벤트가 삭제되었어요!");
    }

    /**
     * Get 공지사항 목록 View
     */
    @GetMapping("/notice/list")
    public String getNoticeListView(Model model, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        NoticeListResult noticeListResult = noticeService.getNoticeListResultForAdmin(pageable);

        model.addAttribute("noticeList", noticeListResult.getNoticeList());
        model.addAttribute(noticeListResult.getPaging());

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
     * Post 공지사항 등록
     */
    @PostMapping("/notice/create")
    public String regNotice(NoticeFormVO noticeFormVO) {
        noticeService.regNotice(modelMapper.map(noticeFormVO, NoticeVO.class));

        return "redirect:/admin/notice/list";
    }

    /**
     * Get 공지사항 수정 View
     */
    @GetMapping("/notice/{noticeId}")
    public String getNoticeModifyView(Model model, @PathVariable Long noticeId) {
        NoticeDetailResult noticeDetail = noticeService.getNoticeDetail(noticeId);

        model.addAttribute("noticeDetail", noticeDetail);
        model.addAttribute("pageTitle", getTitle("공지사항 수정"));
        model.addAttribute("pageContent", "admin/notice/modify/contents");

        return "admin/index";
    }

    /**
     * Post 공지사항 수정
     */
    @PostMapping("/notice/modify")
    public String modNotice(NoticeFormVO noticeFormVO) {
        // 공지사항 수정
        noticeService.modNotice(modelMapper.map(noticeFormVO, NoticeVO.class));

        return "redirect:/notice/" + noticeFormVO.getNoticeId();
    }

    /**
     * Del 공지사항 삭제
     */
    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity delNotice(@PathVariable Long noticeId) {
        // 공지사항 삭제
        noticeService.delNotice(noticeId);

        return ResponseEntity.ok("공지사항이 삭제되었어요!");
    }

    /**
     * Get 강의 제공자 목록 View
     */
    @GetMapping("/professional/list")
    public String getProfessionalListView(Model model, @PageableDefault(sort = "createdAt", size = 9, direction = Sort.Direction.DESC) Pageable pageable) {
        ProfessionalListResult professionalListResult = professionalService.getProfessionalList(pageable);

        model.addAttribute("professionalAccountList", professionalListResult.getProfessionalAccountList());
        model.addAttribute(professionalListResult.getPaging());

        model.addAttribute("pageTitle", getTitle("강의 제공자 목록"));
        model.addAttribute("pageContent", "admin/professional/list/contents");

        return "admin/index";
    }

    /**
     * Post 강의 제공자 활성화
     */
    @PostMapping("/professional/{professionalId}")
    public ResponseEntity modProfessional(@PathVariable Long professionalId) {
        // 등록
        professionalService.modProfessionalById(professionalId);

        return ResponseEntity.ok().build();
    }

    /**
     * Delete 강의 제공자 목록
     */
    @DeleteMapping("/professional/{professionalId}")
    public ResponseEntity delProfessional(@PathVariable Long professionalId) {
        // 삭제
        professionalService.delProfessionalById(professionalId);

        return ResponseEntity.ok().build();
    }
}
