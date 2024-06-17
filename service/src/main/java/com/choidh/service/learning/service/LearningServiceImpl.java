package com.choidh.service.learning.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.entity.ProfessionalAccount;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.repository.ProfessionalAccountRepository;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.joinTables.service.AccountTagService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.learning.vo.LearningDetailVO;
import com.choidh.service.learning.vo.LearningListVO;
import com.choidh.service.learning.vo.ModLearningVO;
import com.choidh.service.learning.vo.RegLearningVO;
import com.choidh.service.notification.eventListener.vo.LearningClosedEvent;
import com.choidh.service.notification.eventListener.vo.LearningCreateEvent;
import com.choidh.service.notification.eventListener.vo.LearningUpdateEvent;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.getLearningNotFoundErrorMessage;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {
    private final LearningRepository learningRepository;
    private final ProfessionalAccountRepository professionalAccountRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AttachmentService attachmentService;
    private final AccountTagService accountTagService;
    private final AccountService accountService;

    /**
     * Learning 목록 조회 By Account's Tags
     */
    @Override
    public List<Learning> getTop12LearningListByTag(Long accountId) {
        Set<AccountTagJoinTable> accountTagList = accountTagService.getTagListByAccountId(accountId);
        List<Tag> tagList = accountTagList.stream().map(AccountTagJoinTable::getTag).collect(Collectors.toList());
        return learningRepository.findTop12ByTagsOrderByRatingDesc(tagList);
    }

    /**
     * Learning 목록 조회. Top 12 By 개설(openingDate) 일시
     */
    @Override
    public List<Learning> getTop12LearningListByOpeningDate() {
        List<Learning> learningList = learningRepository.findTop12ByOpeningIsTrueOrderByOpeningDateDesc();
        return learningRepository.findListByLearningIdsWithTags(learningList);
    }

    /**
     * Learning 목록 조회. Top 12 By Tags Order By 개설(openingDate) 일시 DESC.
     */
    @Override
    public List<Learning> getTop12LearningListByTagsOrderByOpeningDate(Set<Tag> tags) {
        return learningRepository.findTop12LearningListByTagsOrderByOpeningDate(tags);
    }

    /**
     * Learning 목록 조회. Top 12 By Tags Order By Rating DESC
     */
    @Override
    public List<Learning> getTop12LearningListByTagsOrderByRating(Set<Tag> tags) {
        return learningRepository.findTop12LearningListByTagsOrderByRating(tags);
    }

    /**
     * Learning 목록조회 By 평점
     */
    @Override
    public List<Learning> getTop12LearningOrderByRating() {
        List<Learning> learningList = learningRepository.findTop12ByOpeningOrderByRatingDesc(true);
        return learningRepository.findListByLearningIdsWithTags(learningList);
    }

    /**
     * Reg 강의 생성
     */
    @Override
    @Transactional
    public Learning regLearning(RegLearningVO regLearningVO, Long accountId) {
        ProfessionalAccount professionalAccount = professionalAccountRepository.findByAccountId(accountId);

        Learning learning = Learning.builder()
                .title(regLearningVO.getTitle())
                .subscription(regLearningVO.getSubscription())
                .price(regLearningVO.getPrice())
                .mainCategory(regLearningVO.getKategorie())
                .simpleSubscription(regLearningVO.getSimplesubscription())
                .build();
        learning.setProfessionalAccount(professionalAccount);
        learningRepository.save(learning);

        return learning;
    }

    /**
     * 강의 목록조회 By Account Id
     */
    @Override
    public Set<Learning> getLearningList(Long accountId) {
        ProfessionalAccount professionalAccount = professionalAccountRepository.findByAccountIdWithLearningList(accountId);

        if (professionalAccount == null) {
            // TODO : 해당 에러 날 경우 페이지 리다이렉션도 해주긴 해야할 듯 (AdviceController 에서)
            throw new AccessDeniedException("접근 권한이 없는 계정입니다.");
        }

        return professionalAccount.getLearningList();
    }

    /**
     * 강의 단건 조회 By
     */
    @Override
    public Learning getLearningById(Long learningId) {
        return learningRepository.findById(learningId)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(learningId)));
    }

    /**
     * 강의 단건 조회 By Id With Question
     */
    @Override
    public Learning getLearningByIdWithQuestion(Long learningId) {
        return learningRepository.findLearningByIdWithQuestions(learningId);
    }

    @Transactional
    public void fileUpdate(List<MultipartFile> multipartFileList, Account account, Long learningId, AttachmentFileType attachmentFileType) {
        Learning learning = learningRepository.findById(learningId)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(learningId)));

        // 첨부파일 그룹 생성
        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();

        // 첨부파일 파일 DB 생성 및 파일 저장.
        for (MultipartFile multipartFile : multipartFileList) {
            attachmentService.saveFile(attachmentGroup, multipartFile, attachmentFileType);
        }

        learning.setAttachmentGroup(attachmentGroup);
    }

    @Override
    public Learning getLearningDetailForUpdate(Long learningId) {
        return learningRepository.findByIdWithTags(learningId);
    }

    @Override
    public LearningDetailVO getLearningDetail(Long accountId, Long learningId) {
        Learning learning = learningRepository.findLearningDetailById(learningId);
        boolean nowListening = false;

        if (accountId != -1) {
            Account account = accountService.getAccountByIdWithPurchaseHistories(accountId);

            for (PurchaseHistory purchaseHistory : account.getPurchaseHistories()) {
                Learning historyLearning = purchaseHistory.getLearning();

                if (historyLearning.getId().equals(learningId)) {
                    nowListening = true;
                    break;
                }
            }
        }

        AttachmentGroup attachmentGroup = learning.getAttachmentGroup();
        List<AttachmentFile> bannerImageList = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.BANNER);

        if (bannerImageList.isEmpty()) {
            throw new IllegalArgumentException();
        }

        List<AttachmentFile> videoFileList = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.VIDEO);

        // Question 쪽에서 Account fetch 할 수 있도록 확인 필요
        return LearningDetailVO.builder()
                .learning(learning)
                .professionalAccount(learning.getProfessionalAccount())
                .question(learning.getQuestions())
                .reviews(learning.getReviews())
                .bannerImage(bannerImageList.get(0))
                .videoFilesList(videoFileList)
                .nowListening(nowListening)
                .build();
    }

    // 강의 활성화
    @Override
    @Transactional
    public void isOpeningLearning(Long accountId, Long learningId, boolean isOpening) {
        ProfessionalAccount professionalAccount = professionalAccountRepository.findByAccountIdWithLearningList(accountId);
        Learning learning = this.getLearningById(learningId);

        if (
                professionalAccount == null ||
                !professionalAccount.getLearningList().contains(learning)
        ) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        learning.setOpening(isOpening);

        if (isOpening) applicationEventPublisher.publishEvent(new LearningCreateEvent(learning));
        else applicationEventPublisher.publishEvent(new LearningClosedEvent(learning));
    }

    // 영상 삭제.
    @Override
    @Transactional
    public void removeVideo(Long learningId, Long accountId, List<Long> fileIdList) {
        ProfessionalAccount professionalAccount = professionalAccountRepository.findByAccountId(accountId);
        if (professionalAccount == null) throw new IllegalArgumentException("Not Found Professional Account Id is " + accountId);

        Learning learning = this.getLearningById(learningId);
        Long attachmentGroupId = learning.getAttachmentGroup().getId();

        for (Long fileId : fileIdList) {
            // 파일 삭제.
            if (attachmentService.delAttachmentFile(attachmentGroupId, fileId) <= 0) {
                throw new EntityNotFoundException("AttachmentGroup Id is " + attachmentGroupId + "AttachmentFile Id is " + fileId);
            }
        }
    }

    // 강의 정보 수정.
    @Override
    @Transactional
    public void modLearning(ModLearningVO modLearningVO, Long accountId, Long learningId) {
        Learning learning = this.getLearningById(learningId);
        learning.setTitle(modLearningVO.getTitle());
        learning.setSubscription(modLearningVO.getSubscription());
        learning.setSimpleSubscription(modLearningVO.getSimplesubscription());
        learning.setPrice(modLearningVO.getPrice());
        learning.setMainCategory(modLearningVO.getKategorie());

        // TODO: 배너 이미지도 넘겨주는지 확인 필요.

        // 강의가 공개되었다면, 이벤트 발생.
        if (learning.isOpening()) {
            applicationEventPublisher.publishEvent(new LearningUpdateEvent(learning));
        }
    }

    /**
     * 강의 목록조회 By Id List
     */
    @Override
    public List<Learning> getLearningListByIdList(List<Long> learningIdList) {
        return learningRepository.findLearningListByLearningIdList(learningIdList);
    }

    /**
     * 강의 페이징 By Main Category
     */
    @Override
    public Page<Learning> getLearningPagingByCategory(String category, Pageable pageable) {
        category = category.equals("all") ? "" : category;
        return learningRepository.findPagingByCategory(category, pageable);
    }

    /**
     * 강의 페이징 By Main Category And Keyword
     */
    @Override
    public Page<Learning> getPagingByCategoryAndKeyword(String category, String keyword, Pageable pageable) {
        category = category.equals("all") ? "" : category;
        return learningRepository.findPagingByCategoryAndKeyword(category, keyword, pageable);
    }

    /**
     * Get Learning 목록. By View With keyword Learning
     */
    @Override
    public LearningListVO getLearningListByViewWithKeyword(String mainCategory, String subCategory, Pageable pageable) {
        // Learning 페이징
        Page<Learning> learningPage = this.getLearningPagingByCategory(mainCategory, pageable);
        List<Learning> learningList = learningPage.getContent();

        // 강의 이미지 목록 조회
        Map<Long, List<String>> learningImageMap = new HashMap<>();
        for (Learning learning : learningList) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
            if (attachmentFiles.size() != 1) {
                throw new IllegalArgumentException();
            }

            List<String> valueList = learningImageMap.getOrDefault(learning.getId(), new ArrayList<>());

            AttachmentFile attachmentFile = attachmentFiles.get(0);
            valueList.add(attachmentFile.getFullPath(""));
            valueList.add(attachmentFile.getOriginalFileName());
            learningImageMap.put(learning.getId(), valueList);
        }

        String sortProperty = pageable.getSort().toString().contains("openingDate") ? "openingDate" : "rating";

        // 페이지네이션 기본 url
        String defaultButtonUrlBuilder = "/learning/search/" +
                mainCategory +
                "?keyword=" +
                subCategory +
                "&sort=" +
                sortProperty +
                ",desc&page=";

        return LearningListVO.builder()
                .learningPage(learningPage)
                .learningImageMap(learningImageMap)
                .paginationUrl(defaultButtonUrlBuilder)
                .build();
    }

    private String updateVideoTitle(String title) {
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
}
