package com.choidh.service.event.service;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.common.StringUtils;
import com.choidh.service.common.pagination.Paging;
import com.choidh.service.event.entity.Event;
import com.choidh.service.event.repository.EventRepository;
import com.choidh.service.event.vo.EventListResult;
import com.choidh.service.event.vo.RegEventVO;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.vo.LearningAPIVO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

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

        Paging paging = Paging.builder()
                .hasNext(eventPage.hasNext())
                .hasPrevious(eventPage.hasPrevious())
                .number(eventPage.getNumber())
                .totalPages(eventPage.getTotalPages())
                .paginationUrl("/admin/event/list?sort=createdAt,desc&page=")
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
    public Event regEvent(RegEventVO regEventVO) {
        return eventRepository.save(Event.builder()
                .attachmentGroup(regEventVO.getAttachmentGroup())
                .used(regEventVO.isUsed())
                .build());
    }
}
