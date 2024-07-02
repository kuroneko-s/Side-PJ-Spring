package com.choidh.web.admin.vo;

import lombok.*;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventFormVO {
    private String eventId;

    private String title;

    private String description;
}
