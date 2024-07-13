package com.choidh.web.event.controller;

import com.choidh.service.event.entity.Event;
import com.choidh.service.event.service.EventService;
import com.choidh.web.AbstractControllerTestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
class EventControllerTest extends AbstractControllerTestConfig {
    @Autowired
    private EventService eventService;

    @Test
    @DisplayName("이벤트 상세 조회 View (비로그인)")
    public void getEventDetailViewNoneLogin() throws Exception {
        List<Event> eventList = eventService.getEventList();
        assertFalse(eventList.isEmpty());

        Event event = eventList.get(0);

        mockMvc.perform(get("/event/" + event.getId()))
                .andExpect(model().attributeExists("eventDetail"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/detail/index"));
    }
}