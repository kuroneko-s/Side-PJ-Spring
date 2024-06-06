package com.choidh.service.attachment.service;

import com.choidh.service.AbstractServiceTestConfig;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Rollback(value = false)
class AttachmentServiceImplTest extends AbstractServiceTestConfig {
    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Value("${download.path}")
    private String downloadPath;

    @Test
    @DisplayName("AttachmentGroup 생성")
    public void createAttachmentGroup() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        assertNotNull(attachmentGroup);
    }

    @Test
    public void resource_test() throws Exception {
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));
        System.out.println("resource = " + resource);
    }

    @Test
    @DisplayName("파일 저장 (성공)")
    public void saveFile_성공() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "saekano.jpg",
                "saekano.jpg",
                "jpg/image",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.BANNER);
    }

    @Test
    @Rollback
    @DisplayName("파일 저장 (성공 - 영상)")
    public void saveFile_성공_1() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/video/test_movie_2.mov"));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "test_movie-1.mov",
                "test_movie-1.mov",
                "mov/video",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.VIDEO);
    }

    @Test
    @Rollback
    @DisplayName("파일 저장 (성공 - 기타)")
    public void saveFile_성공_2() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/sample.txt"));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "sample.txt",
                "sample.txt",
                "text/plain",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.ETC);
    }

    @Test
    @Rollback
    @DisplayName("파일 저장 (실패 - 이미지)")
    public void saveFile_실패_1() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "saekano.mp4",
                "saekano.mp4",
                "mp4/video",
                resource.getInputStream()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.BANNER);
        });
    }

    @Test
    @DisplayName("파일 목록조회 By 그룹 ID 및 타입")
    public void getAttachmentFiles() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/sample.txt"));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "sample.txt",
                "original sample.txt",
                "text/plain",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.ETC);

        resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/sample2.txt"));

        multipartFile = new MockMultipartFile(
                "sample2.txt",
                "original sample2.txt",
                "text/plain",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.ETC);

        resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/sample3.txt"));

        multipartFile = new MockMultipartFile(
                "sample3.txt",
                "original sample3.txt",
                "text/plain",
                resource.getInputStream()
        );
        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.ETC);

        resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));

        multipartFile = new MockMultipartFile(
                "saekano.jpg",
                "saekano.jpg",
                "jpg/image",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.BANNER);

        theLine();

        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.ETC);
        assertEquals(attachmentFiles.size(), 3);
        assertFalse(attachmentFiles.isEmpty());

        AttachmentFile attachmentFile = attachmentFiles.get(0);
        assertEquals(attachmentFile.getOriginalFileName(), "original sample.txt");
        assertEquals(attachmentFile.getExtension(), "txt");
    }

    @Test
    @DisplayName("파일 목록 카운트. By 그룹 ID 및 파일 타입")
    public void cntAttachmentFiles() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/sample.txt"));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "sample.txt",
                "original sample.txt",
                "text/plain",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.ETC);

        resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/sample2.txt"));

        multipartFile = new MockMultipartFile(
                "sample2.txt",
                "original sample2.txt",
                "text/plain",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.ETC);

        resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/sample3.txt"));

        multipartFile = new MockMultipartFile(
                "sample3.txt",
                "original sample3.txt",
                "text/plain",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.ETC);

        resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));

        multipartFile = new MockMultipartFile(
                "saekano.jpg",
                "saekano.jpg",
                "jpg/image",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.BANNER);

        theLine();

        int totalCount = attachmentService.cntAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.ETC);

        assertEquals(totalCount, 3);
    }

    @Test
    @DisplayName("파일 단건 조회 By 그룹 및 파일 ID")
    public void getAttachmentFileById() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));

        MultipartFile multipartFile = new MockMultipartFile(
                "saekano.jpg",
                "saekano.jpg",
                "jpg/image",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.BANNER);

        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.BANNER);
        assertEquals(attachmentFiles.size(), 1);

        theLine();

        AttachmentFile attachmentFile = attachmentFiles.get(0);
        AttachmentFile result = attachmentService.getAttachmentFileById(attachmentGroup.getId(), attachmentFile.getId());
        assertNotNull(result);
        assertEquals(attachmentFile, result);
    }

    @Test
    @DisplayName("파일 삭제(비활성화) By 그룹 및 파일 ID")
    public void delAttachmentFile() throws Exception {
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));

        MultipartFile multipartFile = new MockMultipartFile(
                "saekano.jpg",
                "saekano.jpg",
                "jpg/image",
                resource.getInputStream()
        );

        attachmentService.saveFile(attachmentGroup, multipartFile, AttachmentFileType.BANNER);

        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.BANNER);
        assertEquals(attachmentFiles.size(), 1);

        theLine();

        AttachmentFile attachmentFile = attachmentFiles.get(0);
        int i = attachmentService.delAttachmentFile(attachmentGroup.getId(), attachmentFile.getId());
        assertEquals(i, 1);
        assertFalse(attachmentFile.isDelete());

        theLine();

        AttachmentFile result = attachmentService.getAttachmentFileById(attachmentGroup.getId(), attachmentFile.getId());
        assertTrue(result.isDelete());

        String fileFullPath = result.getFullPath(downloadPath);

        File file = new File(fileFullPath);
        assertFalse(file.exists());
    }
}