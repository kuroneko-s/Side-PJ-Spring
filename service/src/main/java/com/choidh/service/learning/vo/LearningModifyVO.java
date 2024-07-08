package com.choidh.service.learning.vo;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.vo.VideoFileInfo;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class LearningModifyVO {
    private AttachmentFile bannerFile;

    private List<VideoFileInfo> videoFiles;

    private Learning learning;
}
