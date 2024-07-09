package com.choidh.service.main.vo;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.vo.ImageInfoVO;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class HomeVO {
    private List<AttachmentFile> eventFileList = new ArrayList<>();

    private List<Learning> learningList = new ArrayList<>();

    private List<Learning> newLearningList = new ArrayList<>();

    private Map<Long, ImageInfoVO> learningImageMap = new HashMap<>();

    private Map<Long, ImageInfoVO> newLearningImageMap = new HashMap<>();
}
