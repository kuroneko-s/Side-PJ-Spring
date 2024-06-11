package com.choidh.service.common;

import com.choidh.service.common.exception.DirectoryNotCreateException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

public class FileUtils {
    public static String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return FilenameUtils.getExtension(fileName);
    }

    public static String getFilePath() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int dayOfMonth = now.getDayOfMonth();

        return File.separator + "uploadFiles" + File.separator + year + File.separator + month + File.separator + dayOfMonth + File.separator;
    }

    public static void createDir(String path) {
        File file = new File(path);

        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new DirectoryNotCreateException(path);
            }
        }
    }
}
