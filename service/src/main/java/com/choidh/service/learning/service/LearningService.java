package com.choidh.service.learning.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.vo.ModLearningVO;
import com.choidh.service.learning.vo.RegLearningVO;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LearningService {
    /**
     * 강의 목록조회 By Account's Tags
     */
    List<Learning> getTop12LearningListByTag(Long accountId);

    /**
     * 강의 목록조회 By 생성 일시
     */
    List<Learning> learningOrderByCreateLearning();

    /**
     * 강의 목록조회 By 평점
     */
    List<Learning> learningOrderByRating();

    /**
     * Reg 강의 생성
     */
    Learning regLearning(RegLearningVO regLearningVO, Long accountId);

    /**
     * 강의 목록조회 By Account Id
     */
    List<Learning> getLearningList(Long accountId);

    /**
     * 강의 단건 조회 By
     */
    Learning getLearningById(Long learningId);

    /**
     * 강의 단건 조회 By Id With Question
     */
    Learning getLearningByIdWithQuestion(Long learningId);

    void fileUpdate(List<MultipartFile> videoFileList, Account account, Long learningId, AttachmentFileType attachmentFileType);

    Learning getLearningDetailForUpdate(Long learningId);

    void getLearningDetail(Model model, Long accountId, Long learningId);

    // 강의 활성화.
    void isOpeningLearning(Long accountId, Long learningId, boolean isOpening);

    void removeVideo(Long learningId, Long accountId, List<Long> fileIdList);

    void modLearning(ModLearningVO modLearningVO, Long accountId, Long learningId);

    /**
     * 강의 목록조회 By Id List
     */
    List<Learning> getLearningListByIdList(List<Long> learningIdList);
}
