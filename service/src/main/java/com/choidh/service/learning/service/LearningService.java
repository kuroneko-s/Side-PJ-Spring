package com.choidh.service.learning.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.vo.*;
import com.choidh.service.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface LearningService {
    /**
     * Learning 목록조회 By Account's Tags
     */
    List<Learning> getTop12LearningListByTag(Long accountId);

    /**
     * Learning 목록 조회. Top 12 By 개설(openingDate) 일시. 개설 일시 정렬
     */
    List<Learning> getTop12LearningListByOpeningDate();

    /**
     * Learning 목록 조회. Top 12 By Tags Order By 개설(openingDate) 일시 DESC.
     */
    List<Learning> getTop12LearningListByTagsOrderByOpeningDate(Set<Tag> tags);

    /**
     * Learning 목록 조회. Top 12 By Tags Order By Rating DESC
     */
    List<Learning> getTop12LearningListByTagsOrderByRating(Set<Tag> tagSet);

    /**
     * Learning 목록 조회. 평점 정렬
     */
    List<Learning> getTop12LearningOrderByRating();

    /**
     * Reg 강의 생성
     */
    Learning regLearning(RegLearningVO regLearningVO, Long accountId);

    /**
     * 강의 목록조회 (내가 만든 강의들) By Account Id
     */
    Set<Learning> getLearningListByProfessionalAccount(Long accountId);

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

    /**
     * Get 강의 상세. By View
     */
    LearningDetailVO getLearningDetail(Long accountId, Long learningId);

    /**
     * 강의 학습 페이지 상세. By View
     */
    LearningListenVO getLearningListen(Long accountId, Long learningId);

    // 강의 활성화.
    void isOpeningLearning(Long accountId, Long learningId, boolean isOpening);

    void removeVideo(Long learningId, Long accountId, List<Long> fileIdList);

    void modLearning(ModLearningVO modLearningVO, Long accountId, Long learningId);

    /**
     * 강의 목록조회 By Id List
     */
    List<Learning> getLearningListByIdList(List<Long> learningIdList);

    /**
     * 강의 페이징 By Main Category
     */
    Page<Learning> getPagingByCategory(String category, Pageable pageable);

    /**
     * 강의 페이징 By Main Category And Keyword
     */
    Page<Learning> getPagingByCategoryAndKeyword(String category, String keyword, Pageable pageable);

    /**
     * Get Learning 목록. By View With keyword Learning
     */
    LearningListVO getLearningListByViewWithKeyword(String mainCategory, String subCategory, Pageable pageable);

    /**
     * Get Learning 목록. By View With keyword Learning Of API
     */
    LearningListAPIVO getLearningListByViewWithKeywordOfAPI(String mainCategory, String subCategory, Pageable pageable);
}
