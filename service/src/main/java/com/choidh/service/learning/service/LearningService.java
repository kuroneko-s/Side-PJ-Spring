package com.choidh.service.learning.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.attachment.vo.AttachmentFileType;
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
     * 강의 목록 페이징 (내가 만든 강의들) By Account Id
     */
    LearningListVO getLearningListByProfessionalAccount(Long accountId, Pageable pageable);

    /**
     * 강의 단건 조회 By Learning Id
     */
    Learning getLearningById(Long learningId);

    /**
     * 강의 단건 조회 with Tags By Learning Id
     */
    Learning getLearningWithTagsById(Long learningId);

    /**
     * 강의 영상 조회 By Learning Id
     */
    LearningModifyVO getLearningFilesByLearningId(Long learningId);

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

    /**
     * 강의 내용 수정
     */
    Learning modLearningContext(ModLearningVO modLearningVO, Long accountId, Long learningId);

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

    /**
     * Del Learning 비활성화. By 강의 제공자 Id
     */
    void delLearningByProfessionalId(Long professionalId);

    /**
     * mod Learning 활성화. By 강의 제공자 Id
     */
    void modLearningByProfessionalId(Long professionalId);

    /**
     * 강의 배너 이미지 수정
     */
    void modLearningBannerImage(Long learningId, MultipartFile bannerFile);

    /**
     * 강의 영상 비디오 수정
     */
    void modLearningVideo(Long learningId, String title, Integer order, MultipartFile videoFile);
}
