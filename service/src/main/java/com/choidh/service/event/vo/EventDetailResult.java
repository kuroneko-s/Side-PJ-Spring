package com.choidh.service.event.vo;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventDetailResult {
    private Long id;

    private String title;

    private String description;

    private String bannerImageUrl;

    private String bannerImageAlt;

    private String contextImageUrl;

    private String contextImageAlt;
}
