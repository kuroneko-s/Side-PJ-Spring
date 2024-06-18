package com.choidh.service.attachment.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AttachmentFileRepositoryTest extends AbstractRepositoryTestConfig {
    private final AttachmentFileRepository attachmentFileRepository;
    private final AttachmentGroupRepository attachmentGroupRepository;

    private AttachmentGroup createAttachmentGroup() {
        return attachmentGroupRepository.save(new AttachmentGroup());
    }

    private AttachmentFile createAttachmentFile(AttachmentGroup attachmentGroup, AttachmentFileType attachmentFileType) {
        return attachmentFileRepository.save(AttachmentFile.builder()
                        .attachmentGroup(attachmentGroup)
                        .fileSize(1000L)
                        .path("/root/usr")
                        .originalFileName("sampleFile")
                        .fileName("000000100000001")
                        .extension(".txt")
                        .isDelete(false)
                        .attachmentFileType(attachmentFileType)
                .build());
    }

    @Test
    @DisplayName("파일 그룹 생성")
    public void simpleGroupCreateTest() throws Exception {
        this.createAttachmentGroup();
    }

    @Test
    @DisplayName("파일 목록 조회 By 그룹 ID 및 파일 타입")
    public void findAllByGroupAndType() throws Exception {
        AttachmentGroup attachmentGroup = this.createAttachmentGroup();

        for (int i = 0; i < 10; i++) {
            createAttachmentFile(attachmentGroup, AttachmentFileType.VIDEO);
        }

        for (int i = 0; i < 10; i++) {
            createAttachmentFile(attachmentGroup, AttachmentFileType.ETC);
        }

        for (int i = 0; i < 10; i++) {
            createAttachmentFile(attachmentGroup, AttachmentFileType.BANNER);
        }

        theLine();
        List<AttachmentFile> bannerFiles = this.attachmentFileRepository.findAllByGroupAndType(attachmentGroup.getId(), AttachmentFileType.BANNER);
        assertFalse(bannerFiles.isEmpty());
        assertEquals(bannerFiles.size(), 10);

        theLine();
        List<AttachmentFile> videoFiles = this.attachmentFileRepository.findAllByGroupAndType(attachmentGroup.getId(), AttachmentFileType.VIDEO);
        assertFalse(videoFiles.isEmpty());
        assertEquals(bannerFiles.size(), 10);

        theLine();
        List<AttachmentFile> etcFiles = this.attachmentFileRepository.findAllByGroupAndType(attachmentGroup.getId(), AttachmentFileType.ETC);
        assertFalse(etcFiles.isEmpty());
        assertEquals(bannerFiles.size(), 10);
    }

    @Test
    @DisplayName("파일 단건 조회 By 그룹 및 파일 ID")
    public void findByFileId() throws Exception {
        AttachmentGroup attachmentGroup = this.createAttachmentGroup();
        Long targetId = -1L;
        AttachmentFile target = null;

        for (int i = 0; i < 35; i++) {
            AttachmentFile attachmentFile = createAttachmentFile(attachmentGroup, AttachmentFileType.VIDEO);
            if ( i == 32 ) {
                target = attachmentFile;
                targetId = attachmentFile.getId();
            }
        }

        theLine();

        AttachmentFile result = attachmentFileRepository.findByFileId(targetId);

        assertEquals(target, result);
    }

    @Test
    @DisplayName("파일 삭제(비활성화) By 그룹 및 파일 ID")
    public void deleteByFileId() throws Exception {
        AttachmentGroup attachmentGroup = this.createAttachmentGroup();
        AttachmentFile target = null;

        for (int i = 0; i < 35; i++) {
            AttachmentFile attachmentFile = createAttachmentFile(attachmentGroup, AttachmentFileType.VIDEO);
            if ( i == 32 ) target = attachmentFile;
        }

        theLine();

        int updateResult = attachmentFileRepository.deleteByFileId(target.getId());

        assertEquals(updateResult, 1);

        AttachmentFile attachmentFile = attachmentFileRepository.findByFileId(target.getId());

        assertTrue(attachmentFile.isDelete());
        assertEquals(target, attachmentFile);
    }

    @Test
    @DisplayName("파일 목록 카운트. By 그룹 ID 및 파일 타입")
    public void countByGroupAndType() throws Exception {
        AttachmentGroup attachmentGroup = this.createAttachmentGroup();

        for (int i = 0; i < 35; i++) {
            createAttachmentFile(attachmentGroup, AttachmentFileType.VIDEO);
        }

        theLine();

        int result = attachmentFileRepository.countByGroupAndType(attachmentGroup.getId(), AttachmentFileType.VIDEO);

        assertEquals(result,35);
    }
}