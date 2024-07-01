package com.choidh.service.event.vo;

import com.choidh.service.common.pagination.Paging;
import com.choidh.service.event.entity.Event;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter
@Builder @NoArgsConstructor
@AllArgsConstructor
public class EventListResult {
    private List<Event> eventList;

    private Map<Long, String> imageMap;

    private Paging paging;
}
