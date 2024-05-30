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
    default String updateVideoTitle(String title) {
        String regExp = "[a-zA-Zㄱ-ㅎ가-힣ㅏ-ㅣ\\s_](.mp3|.mp4|mkv)";
        String regExpNot = "[0-9]+(.)[0-9]+";
        String number = title.replaceAll(regExp, "").trim();
        String notNumber = title.replaceAll(regExpNot, "").trim();
        int i = number.indexOf("-");
        int strIndex = number.indexOf(notNumber.charAt(0));

        String f = number.substring(0, i); //앞
        String e = number.substring(i + 1, strIndex); //뒤
        String newf = "";
        String newe = "";

        if (f.length() <= 1) newf = 0 + f;
        else newf = f;

        if (e.length() <= 1) newe = 0 + e;
        else newe = e;

        return newf + "-" + newe + notNumber;
    }

    /**
     * 유저의 Tag 기반 강의 목록조회 Top 12
     */
    List<Learning> getTop12LearningListByTag(Long accountId);

    List<Learning> learningOrderByCreateLearning();

    List<Learning> learningOrderByRating();

    /**
     * Reg 강의 생성
     */
    Learning regLearning(RegLearningVO regLearningVO, Long accountId);

    // 강의 목록 조회
    List<Learning> getLearningList(Long accountId);

    // 강의 단건 조회 By Id
    Learning getLearningById(Long id);

    /**
     * 강의 단건 조회 By Id With Question and Video files
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
