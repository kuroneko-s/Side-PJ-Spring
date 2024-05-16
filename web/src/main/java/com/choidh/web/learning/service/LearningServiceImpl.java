package com.choidh.web.learning.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.common.AppConstant;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.notification.vo.LearningClosedEvent;
import com.choidh.service.notification.vo.LearningCreateEvent;
import com.choidh.service.notification.vo.LearningUpdateEvent;
import com.choidh.service.question.entity.Question;
import com.choidh.service.question.repository.QuestionRepository;
import com.choidh.service.review.entity.Review;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.repository.TagRepository;
import com.choidh.service.video.entity.Video;
import com.choidh.service.video.repository.VideoRepository;
import com.choidh.web.learning.vo.LearningFormVO;
import com.choidh.web.tag.vo.TagForm;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LearningServiceImpl implements LearningService {
    private final LearningRepository learningRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final VideoRepository videoRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final QuestionRepository questionRepository;

    final File absoluteFile = new File("");
    final String ROOT_PATH = absoluteFile.getAbsolutePath();

    @Transactional
    public Learning saveLearning(LearningFormVO learningFormVO, Account account){
        Learning learning = new Learning();

        learning.setTitle(learningFormVO.getTitle());
        learning.setSubscription(learningFormVO.getSubscription());
        learning.setLecturerName(learningFormVO.getLecturerName());
        learning.setLecturerDescription(learningFormVO.getLecturerDescription());
        learning.setPrice(learningFormVO.getPrice());
        learning.setKategorie(learningFormVO.getKategorie());
        learning.setSimplesubscription(learningFormVO.getSimplesubscription());
        learning.setCreateLearning(LocalDateTime.now());
        Learning newLearning = learningRepository.save(learning);
        newLearning.setAccount(account);

        return newLearning;
    }

    @Transactional
    public void saveLearningTags(Learning learning, Tag tag){
        Tag newTag = tagRepository.save(tag);
        learning.getTags().add(newTag);
    }

    @Transactional
    public void removeLearningTags(Learning learning, TagForm tagForm) {
        Tag tag = tagRepository.findByTitle(tagForm.getTitle());
        learning.getTags().remove(tag);
    }

    @Transactional
    public void saveVideo(List<MultipartFile> videoFileList, Account account, Learning learning) throws IOException {
        @NotNull final String title = learning.getTitle().replaceAll(" ", "_");
        // TODO : 폴더를 기존 회원 아이디 + 영상 아이디로 관리하던걸 파일명으로 변경하고 파일 관련 테이블을 별도로 개설한 후 테이블을 별도로 관리 진행해줘야함. 폴더 구분은 날짜로만 대분류로 나누고 내부에선 파일명으로 나눠주는 식으로 변경
        final String accountPath = ROOT_PATH + "/src/main/resources/static/sampleData/video"/* + account.getId()*/;
        final String accountLearningPath = ROOT_PATH + "/src/main/resources/static/sampleData/video"/* + account.getId() + "/" + learning.getId()*/;
        learning.setVideoCount(learning.getVideoCount() + videoFileList.size());

        //directory checking
        File accountFolder = new File(accountPath);
        if(!accountFolder.isDirectory()){
            accountFolder.mkdir();
        }
        File accountLearningfolder = new File(accountLearningPath);
        if(!accountLearningfolder.isDirectory()){
            accountLearningfolder.mkdir();
        }

        for (MultipartFile file : videoFileList){ //save the vide files in server folder
            Resource resource = file.getResource();
            try(
                    BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                            accountLearningPath + "/" + resource.getFilename()), 1024 * 500)
            ){
                Video video = new Video();
                video.setVideoServerPath(accountLearningPath +"/" + resource.getFilename());
                video.setVideoSize(file.getSize() > 0 ? file.getSize() : 0);
                video.setVideoTitle(updateVideoTitle(Objects.requireNonNull(file.getOriginalFilename())));
                video.setSaveTime(LocalDateTime.now());

                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();

                Video newVideo = videoRepository.save(video);
                learning.setVideos(newVideo);
            } catch (IOException e) {
                throw new IOException(e);
            }
        } //save the vide files in server folder

        learning.setUploadVideo(LocalDateTime.now());
        learningRepository.save(learning);
    }

    @Transactional
    public void saveBanner(MultipartFile banner, Account account, Learning learning) throws IOException{
        final String accountPath = ROOT_PATH + "/src/main/resources/static/video/" + account.getId();
        final String accountLearningPath = ROOT_PATH +  "/src/main/resources/static/video/" + account.getId() + "/" + learning.getId();

        learning.setBannerServerPath(accountLearningPath + "/" + banner.getResource().getFilename());

        try { //banner byte encoding
            byte[] bytes = Base64.getEncoder().encode(banner.getBytes());
            // byte[] bytes = Base64.encodeBase64(banner.getBytes()); // -> 기존코드
            String bannerStr = new String(bytes, StandardCharsets.UTF_8);
            learning.setBannerBytes(bannerStr);
        }catch (IOException e){
            throw new IOException(e);
        } //banner byte encoding

        //directory checking
        File accountFolder = new File(accountPath);
        if(!accountFolder.isDirectory()){
            accountFolder.mkdir();
        }
        File accountLearningfolder = new File(accountLearningPath);
        if(!accountLearningfolder.isDirectory()){
            accountLearningfolder.mkdir();
        }

        Resource resource = banner.getResource();
        try(
                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                        accountLearningPath + "/" + resource.getFilename()), 1024 * 5)
        ){ //save the banner img in server folder
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new IOException(e);
        } //save the banner img in server folder

        learningRepository.save(learning);
    }

    @Transactional
    public void updateLearningScript(LearningFormVO learningFormVO, Account account, Long id) {
        Learning learning = learningRepository.findById(id).orElseThrow();
        Account newAccount = accountRepository.findById(account.getId()).orElseThrow();

        final String learningPathBefore = ROOT_PATH + "/src/main/resources/static/video/" + account.getId() + "/" + learning.getId();

        learning.setTitle(learningFormVO.getTitle());
        learning.setSubscription(learningFormVO.getSubscription());
        learning.setLecturerName(learningFormVO.getLecturerName());
        learning.setLecturerDescription(learningFormVO.getLecturerDescription());
        learning.setPrice(learningFormVO.getPrice());
        learning.setKategorie(learningFormVO.getKategorie());
        learning.setSimplesubscription(learningFormVO.getSimplesubscription());
        learning.setUpdateLearning(LocalDateTime.now());
        learning.setAccount(newAccount);

        try {
            String accountIdStr = newAccount.getId() + "";
            int i = learning.getBannerServerPath().indexOf(accountIdStr);
            String firstStr = learning.getBannerServerPath().substring(0, i);
            String secondStr = learning.getBannerServerPath().substring(i + accountIdStr.length());
            learning.setBannerServerPath(firstStr + accountIdStr + secondStr);
        }catch (NullPointerException e){
            log.info("banner image serverPath 미지정 = 기본 이미지 값 사용중");
        }

        if(learning.isStartingLearning()){
            applicationEventPublisher.publishEvent(new LearningUpdateEvent(learning));
        }
    }

    @Async
    @Transactional
    public void inoutStream(File file, String learningPathAfter, String s) throws IOException {
        try (
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(learningPathAfter + "/" + s), 1024 * 500)
        ){
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (!file.isDirectory()) {
                file.delete();
            }else{
                FileUtils.deleteDirectory(file);
            }
        }
    }

    @Transactional
    public void startLearning(Long id) {
        Optional<Learning> byId = learningRepository.findById(id);
        Learning learning = byId.orElseThrow();

        learning.setStartingLearning(true);
        learning.setClosedLearning(false);
        learning.setOpenLearning(LocalDateTime.now());

        applicationEventPublisher.publishEvent(new LearningCreateEvent(learning));
    }

    @Transactional
    public void closeLearning(Long id) {
        Optional<Learning> byId = learningRepository.findById(id);
        Learning learning = byId.orElseThrow();

        learning.setStartingLearning(false);
        learning.setClosedLearning(true);
        learning.setCloseLearning(LocalDateTime.now());

        applicationEventPublisher.publishEvent(new LearningClosedEvent(learning));
    }

    @Transactional
    public boolean checkOpenTimer(boolean startingLearning, boolean closedLearning, boolean contains) {
        return !startingLearning && closedLearning && contains;
    }

    @Transactional
    public Object checkCloseTimer(boolean startingLearning, boolean closedLearning, boolean contains) {
        return startingLearning && !closedLearning && contains;
    }

    @Transactional
    public List<String> getContentsTitle(Learning learning) {
        List<String> contentTitle = new ArrayList<>();
        String regExp = "[a-zA-Zㄱ-ㅎ가-힣ㅏ-ㅣ\\s_](.mp3|.mp4|mkv)";
        String regExpNot = "[0-9]+(.)[0-9]+";
        learning.getVideos().stream().map(Video::getVideoTitle)
                .forEach(s -> {
                    String number = s.replaceAll(regExp, "").trim();
                    String notNumber = s.replaceAll(regExpNot, "").trim();
                    int i = number.indexOf("-");
                    int strIndex = number.indexOf(notNumber.charAt(0));

                    String f = number.substring(0, i); //앞
                    String e = number.substring(i+1, strIndex); //뒤
                    String newf = "";
                    String newe = "";

                    if(f.length() <= 1){
                        newf = 0 + f;
                    }else {
                        newf = f;
                    }

                    if(e.length() <= 1){
                        newe = 0 + e;
                    }else {
                        newe = e;
                    }
                    contentTitle.add(newf + "-" + newe + notNumber);
                });

        return contentTitle;
    }

    @Transactional
    public void setReview(Review review, Learning learning) {
        learning.setReviews(review);
        double sum = learning.getReviews().stream().mapToDouble(Review::getRating).sum();
        int ratingLength = learning.getReviews().size();
        learning.setRating((float) (sum / ratingLength));
    }

    @Transactional
    public List<Learning> findLearningByIdAndLecture(List<String> id_split, List<String> lecture_split) {
        List<Learning> learningList = new ArrayList<>();

        for (int i = 0; i < id_split.size(); i++) {
            learningList.add(learningRepository.findByIdAndLecturerName(Long.valueOf(id_split.get(i)), lecture_split.get(i)));
        }

        return learningList;
    }

    @Transactional
    public Account saveQuestion(Question question, Account account, Learning learning) {
        Account newAccount = accountRepository.findAccountWithQuestion(account.getId());

        if (newAccount == null)
            throw new IllegalArgumentException(AppConstant.getAccountNotFoundErrorMessage(account.getId()));

        question.setS_name(newAccount.getNickname());
        question.setTime_questionTime(LocalDateTime.now());

        Question newQuestion = questionRepository.save(question);

        newQuestion.setAccount(newAccount);
        newQuestion.setLearning(learning);

        newAccount.getQuestions().add(newQuestion);
        learning.getQuestions().add(newQuestion);

        return newAccount;
    }

    @Transactional
    public Learning removeVideo(Learning learning, Video video, Account account) {

        final String learningPath = ROOT_PATH + "/src/main/resources/static/video/" + account.getId() + "/" + learning.getId();

        File file = new File(learningPath + "/" + video.getVideoTitle());
        file.delete();

        learning.getVideos().remove(video);
        videoRepository.deleteById(video.getId());

        return learning;
    }
}
