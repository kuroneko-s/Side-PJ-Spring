package com.choidh.service.event.vo;

import com.choidh.service.attachment.entity.AttachmentGroup;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RegEventVO {
    private AttachmentGroup attachmentGroup;

    private boolean used = true;
}
