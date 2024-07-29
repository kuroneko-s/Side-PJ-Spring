package com.choidh.service.learning.vo.web;

import com.choidh.service.learning.entity.Learning;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningListenVO {
    private Learning learning;

    private Set<String> videoTitleSet;

    private String videoSrc;

    private String playingVideo;

    private Map<String, Long> videoFileIdMap;
}
