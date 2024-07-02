package com.choidh.service.event.vo;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class EventVO {
    private String eventId;

    private String title;

    private String description;
}
