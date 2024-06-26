package com.choidh.service.event.service;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.event.entity.Event;
import com.choidh.service.event.repository.EventRepository;
import com.choidh.service.event.vo.RegEventVO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
