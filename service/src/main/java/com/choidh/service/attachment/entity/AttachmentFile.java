package com.choidh.service.attachment.entity;

import com.choidh.service.annotation.Name;
import com.choidh.service.common.entity.BaseDateEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AttachmentFile extends BaseDateEntity {
    @Id
    @GeneratedValue
    @Column(name = "attachment_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_group_id")
    private AttachmentGroup attachmentGroup;

    @Name(name = "파일 크기")
    private Long fileSize;

    @Name(name = "경로", description = "기본 하위 경로는 properties로 받아올 것.")
    private String path;

    @Name(name = "파일 명", description = "업로드 때의 ")
    private String originalFileName;

    @Name(name = "파일 명(재설정)", description = "그룹 id + 파일 id로 관리할 예정")
    private String fileName;

    @Name(name = "파일 확장자")
    private String extension;

    private boolean isDelete = false;

    @Enumerated(EnumType.STRING)
    private AttachmentFileType attachmentFileType;

    public AttachmentFile(AttachmentGroup attachmentGroup) {
        this.attachmentGroup = attachmentGroup;
    }

    public String getFullPath(String basePath) {
        return basePath + this.path + fileName + "." + extension;
    }
}