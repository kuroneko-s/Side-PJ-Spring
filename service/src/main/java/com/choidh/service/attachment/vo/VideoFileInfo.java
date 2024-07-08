package com.choidh.service.attachment.vo;

import com.choidh.service.common.annotation.Name;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class VideoFileInfo {
    private String videoName;

    private String videoSrc;

    @Name(name = "목차")
    private String contents;

    private String order;
}
