package com.choidh.service.event.service;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.vo.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.common.pagination.Paging;
import com.choidh.service.event.entity.Event;
import com.choidh.service.event.repository.EventRepository;
import com.choidh.service.event.vo.EventDetailResult;
import com.choidh.service.event.vo.EventListResult;
import com.choidh.service.event.vo.EventVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.choidh.service.common.vo.AppConstant.getEventNotFoundErrorMessage;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private EventRepository eventRepository;

    /**
     * 이벤트 목록 조회.
     */
    @Override
    public List<Event> getEventList() {
        return eventRepository.findAll();
    }

    /**
     * 이벤트 목록 페이징.
     */
    @Override
    public EventListResult getEventListResult(Pageable pageable) {
        Page<Event> eventPage = eventRepository.findListWithPaging(pageable);

        // 이미지 목록 조회
        Map<Long, String> imageMap = new HashMap<>();
        for (Event event : eventPage.getContent()) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(event.getAttachmentGroup().getId(), AttachmentFileType.EVENT_BANNER);
            if (attachmentFiles.size() != 1) {
                throw new IllegalArgumentException();
            }

            AttachmentFile attachmentFile = attachmentFiles.get(0);
            imageMap.put(event.getId(), attachmentFile.getFullPath(""));
        }

        String paginationUrl = "/admin/event/list?sort=createdAt,asc&page=";
        for (Sort.Order order : pageable.getSort()) {
            Sort.Direction direction = order.getDirection();

            if (direction.isDescending()) {
                paginationUrl = "/admin/event/list?sort=createdAt,desc&page=";
            }
        }

        Paging paging = Paging.builder()
                .hasNext(eventPage.hasNext())
                .hasPrevious(eventPage.hasPrevious())
                .number(eventPage.getNumber())
                .totalPages(Math.max(eventPage.getTotalPages() - 1, 0))
                .paginationUrl(paginationUrl)
                .build();

        return EventListResult.builder()
                .eventList(eventPage.getContent())
                .imageMap(imageMap)
                .paging(paging)
                .build();
    }

    /**
     * 이벤트 목록 조회 With 첨부 파일.
     */
    @Override
    public List<AttachmentFile> getEventFileList() {
        List<Event> eventList = eventRepository.findListByUsedIsTrue();
        List<AttachmentFile> resultList = new ArrayList<>();

        for (Event event : eventList) {
            AttachmentGroup attachmentGroup = event.getAttachmentGroup();
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.EVENT_BANNER);
            resultList.add(attachmentFiles.get(0));
        }

        return resultList;
    }

    /**
     * 이벤트 생성.
     */
    @Override
    @Transactional
    public Event regEvent(EventVO eventVO, Map<String, MultipartFile> fileMap) {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        attachmentService.saveFile(attachmentGroup, fileMap.get("banner"), AttachmentFileType.EVENT_BANNER);
        attachmentService.saveFile(attachmentGroup, fileMap.get("context"), AttachmentFileType.EVENT_CONTEXT);

        return eventRepository.save(
                Event.builder()
                        .attachmentGroup(attachmentGroup)
                        .used(true)
                        .title(eventVO.getTitle())
                        .description(eventVO.getDescription())
                        .build());
    }

    /**
     * 이벤트 단건 조회
     */
    @Override
    public EventDetailResult getEventDetail(Long eventId) {
        EventDetailResult eventDetailResult = new EventDetailResult();

        Event event = this.getEventById(eventId);
        eventDetailResult.setId(event.getId());
        eventDetailResult.setTitle(event.getTitle());
        eventDetailResult.setDescription(event.getDescription());

        // 이벤트 배너 이미지 조회
        List<AttachmentFile> bannerFiles = attachmentService.getAttachmentFiles(event.getAttachmentGroup().getId(), AttachmentFileType.EVENT_BANNER);
        if (bannerFiles.size() != 1) {
            throw new IllegalArgumentException();
        }

        AttachmentFile bannerFile = bannerFiles.get(0);
        eventDetailResult.setBannerImageUrl(bannerFile.getFullPath(""));
        eventDetailResult.setBannerImageAlt(bannerFile.getOriginalFileName());

        // 이벤트 내용 이미지 조회
        List<AttachmentFile> contextFiles = attachmentService.getAttachmentFiles(event.getAttachmentGroup().getId(), AttachmentFileType.EVENT_CONTEXT);
        if (contextFiles.size() != 1) {
            throw new IllegalArgumentException();
        }

        AttachmentFile contextFile = contextFiles.get(0);
        eventDetailResult.setContextImageUrl(contextFile.getFullPath(""));
        eventDetailResult.setContextImageAlt(contextFile.getOriginalFileName());

        return eventDetailResult;
    }

    /**
     * 이벤트 단건 조회
     */
    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(getEventNotFoundErrorMessage(eventId)));
    }

    /**
     * 이벤트 수정.
     */
    @Override
    @Transactional
    public Event modEvent(EventVO eventVO, Map<String, MultipartFile> fileMap) {
        // 기존 파일들 조회해서 삭제한 후 다시 저장해줘야함...
        Long eventId = Long.valueOf(eventVO.getEventId());
        Event event = this.getEventById(eventId);
        AttachmentGroup attachmentGroup = event.getAttachmentGroup();

        if (fileMap.containsKey("banner")) {
            MultipartFile afterBannerFile = fileMap.get("banner");
            List<AttachmentFile> bannerFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.EVENT_BANNER);
            AttachmentFile beforeBannerFile = bannerFiles.get(0);

            // 파일 크기가 동일하다면 같은 파일로 취급.
            if (afterBannerFile.getSize() != beforeBannerFile.getFileSize()) {
                // 이벤트 배너 이미지 삭제
                attachmentService.delAttachmentFile(beforeBannerFile.getId());

                // 새로운 파일 저장.
                attachmentService.saveFile(attachmentGroup, afterBannerFile, AttachmentFileType.EVENT_BANNER);
            }
        }

        if (fileMap.containsKey("context")) {
            MultipartFile afterContextFile = fileMap.get("context");
            List<AttachmentFile> contextFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.EVENT_CONTEXT);
            AttachmentFile beforeContextFile = contextFiles.get(0);

            // 파일 크기가 동일하다면 같은 파일로 취급.
            if (afterContextFile.getSize() != beforeContextFile.getFileSize()) {
                // 이벤트 배너 이미지 삭제
                attachmentService.delAttachmentFile(beforeContextFile.getId());

                // 새로운 파일 저장.
                attachmentService.saveFile(attachmentGroup, afterContextFile, AttachmentFileType.EVENT_CONTEXT);
            }
        }

        event.setTitle(eventVO.getTitle());
        event.setDescription(eventVO.getDescription());

        return event;
    }

    /**
     * 이벤트 삭제
     */
    @Override
    @Transactional
    public void delEvent(Long eventId) {
        // 이벤트 사용 유무 비활성화.
        eventRepository.delEvent(eventId);

        // 파일은 유지...
    }
}
