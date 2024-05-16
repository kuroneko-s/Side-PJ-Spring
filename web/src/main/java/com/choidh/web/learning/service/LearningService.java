package com.choidh.web.learning.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.question.entity.Question;
import com.choidh.service.review.entity.Review;
import com.choidh.service.tag.entity.Tag;
import com.choidh.web.learning.vo.LearningFormVO;
import com.choidh.web.tag.vo.TagForm;
import com.choidh.service.video.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    Learning saveLearning(LearningFormVO learningFormVO, Account account);

    void saveLearningTags(Learning learning, Tag tag);

    void removeLearningTags(Learning learning, TagForm tagForm);

    void saveVideo(List<MultipartFile> videoFileList, Account account, Learning learning) throws IOException;

    void saveBanner(MultipartFile banner, Account account, Learning learning) throws IOException;

    void updateLearningScript(LearningFormVO learningFormVO, Account account, Long id);

    void inoutStream(File file, String learningPathAfter, String s) throws IOException;

    void startLearning(Long id);

    void closeLearning(Long id);

    boolean checkOpenTimer(boolean startingLearning, boolean closedLearning, boolean contains);

    Object checkCloseTimer(boolean startingLearning, boolean closedLearning, boolean contains);

    List<String> getContentsTitle(Learning learning);

    void setReview(Review review, Learning learning);

    List<Learning> findLearningByIdAndLecture(List<String> id_split, List<String> lecture_split);

    Account saveQuestion(Question    question, Account account, Learning learning);

    Learning removeVideo(Learning learning, Video video, Account account);
}
