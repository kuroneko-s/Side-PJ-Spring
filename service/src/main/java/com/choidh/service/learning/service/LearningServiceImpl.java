package com.choidh.service.learning.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.attachment.vo.AttachmentFileType;
import com.choidh.service.attachment.vo.VideoFileInfo;
import com.choidh.service.common.pagination.Paging;
import com.choidh.service.common.utiles.StringUtils;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.joinTables.service.AccountTagService;
import com.choidh.service.joinTables.service.LearningTagService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.learning.vo.*;
import com.choidh.service.notification.eventListener.vo.LearningClosedEvent;
import com.choidh.service.notification.eventListener.vo.LearningCreateEvent;
import com.choidh.service.notification.eventListener.vo.LearningUpdateEvent;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.professional.service.ProfessionalService;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.service.TagService;
import com.choidh.service.tag.vo.RegTagVO;
import com.choidh.service.tag.vo.TagBinder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.choidh.service.common.vo.AppConstant.getLearningNotFoundErrorMessage;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {
    @Autowired private LearningRepository learningRepository;
    @Autowired private ApplicationEventPublisher applicationEventPublisher;
    @Autowired private AttachmentService attachmentService;
    @Autowired private AccountTagService accountTagService;
    @Autowired private AccountService accountService;
    @Autowired private ProfessionalService professionalService;
    @Autowired private LearningTagService learningTagService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TagService tagService;

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
        ProfessionalAccount professionalAccount = professionalService.getProfessionalByAccountId(accountId);

        AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();

        // 태그 입력값 분석
        List<TagBinder> tagBinderList;
        try {
            tagBinderList = objectMapper.readValue(regLearningVO.getSkills(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(regLearningVO.getSkills());
        }

        // 강의 생성
        Learning learning = learningRepository.save(Learning.builder()
                .title(regLearningVO.getTitle())
                .subscription(regLearningVO.getSubscription())
                .price(regLearningVO.getPrice())
                .mainCategory(regLearningVO.getMainCategory())
                .simpleSubscription(regLearningVO.getSimpleSubscription())
                .attachmentGroup(attachmentGroup)
                .opening(false)
                .tags(new HashSet<>())
                .questions(new HashSet<>())
                .reviews(new HashSet<>())
                .purchaseHistories(new HashSet<>())
                .carts(new HashSet<>())
                .notices(new HashSet<>())
                .professionalAccount(professionalAccount)
                .build());

        // 태그 강의에 추가 및 조인 테이블 생성
        for (TagBinder tagBinder : tagBinderList) {
            Tag tag = tagService.regTag(RegTagVO.builder()
                    .title(tagBinder.getValue())
                    .build());

            LearningTagJoinTable learningTagJoinTable = learningTagService.regLearningTagJointable(learning, tag);
            learning.setTags(learningTagJoinTable);
        }

        professionalAccount.getLearningSet().add(learning);

        return learning;
    }

    /**
     * 강의 목록조회 By Account Id
     */
    @Override
    public LearningListVO getLearningListByProfessionalAccount(Long accountId, Pageable pageable) {
        ProfessionalAccount professionalAccount = professionalService.getProfessionalByAccountId(accountId);

        if (professionalAccount == null) {
            throw new AccessDeniedException("접근 권한이 없는 계정입니다.");
        }

        Page<Learning> learningPage = learningRepository.findByProfessionalAccountId(professionalAccount.getId(), pageable);
        List<Learning> learningList = learningPage.getContent();

        // 강의 영상 등록 유무
        Map<Long, Boolean> imageUploadMap = new HashMap<>();
        // 강의 이미지 목록 조회
        Map<Long, List<String>> learningImageMap = new HashMap<>();
        for (Learning learning : learningList) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);

            // 강의 이미지 정보가 없으면 갓 생성한 경우니 대응해줘야함.
            List<String> valueList = learningImageMap.getOrDefault(learning.getId(), new ArrayList<>());

            if (attachmentFiles.size() != 1) {
                // 기본 이미지 지정
                learningImageMap.put(learning.getId(), valueList);
                valueList.add("/images/logo.png");
                valueList.add("기본 이미지");
                learningImageMap.put(learning.getId(), valueList);
            } else {
                AttachmentFile attachmentFile = attachmentFiles.get(0);
                valueList.add(attachmentFile.getFullPath(""));
                valueList.add(attachmentFile.getOriginalFileName());
                learningImageMap.put(learning.getId(), valueList);
            }

            if (attachmentFiles.isEmpty()) {
                imageUploadMap.put(learning.getId(), false);
            } else {
                List<AttachmentFile> videoFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.VIDEO);
                if (videoFiles.isEmpty()) {
                    imageUploadMap.put(learning.getId(), false);
                } else {
                    imageUploadMap.put(learning.getId(), true);
                }
            }
        }

        // 페이지네이션 기본 url
        String paginationUrl = "/professional/learning/list?sort=createdAt,asc&page=";
        for (Sort.Order order : pageable.getSort()) {
            Sort.Direction direction = order.getDirection();

            if (direction.isDescending()) {
                paginationUrl = "/professional/learning/list?sort=createdAt,desc&page=";
            }
        }

        Paging paging = Paging.builder()
                .hasNext(learningPage.hasNext())
                .hasPrevious(learningPage.hasPrevious())
                .number(learningPage.getNumber())
                .totalPages(Math.max(learningPage.getTotalPages() - 1, 0))
                .paginationUrl(paginationUrl)
                .build();

        return LearningListVO.builder()
                .learningList(learningPage.getContent())
                .learningImageMap(learningImageMap)
                .imageUploadMap(imageUploadMap)
                .paging(paging)
                .build();
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
     * 강의 단건 조회 with Tags By Learning Id
     */
    @Override
    public Learning getLearningWithTagsById(Long learningId) {
        return learningRepository.findByIdWithTags(learningId);
    }

    /**
     * 강의 영상 조회 By Learning Id
     */
    @Override
    public LearningModifyVO getLearningFilesByLearningId(Long learningId) {
        Learning learning = this.getLearningById(learningId);
        AttachmentGroup attachmentGroup = learning.getAttachmentGroup();

        List<AttachmentFile> bannerFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.BANNER);
        List<AttachmentFile> videoFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.VIDEO);

        LearningModifyVO learningModifyVO = new LearningModifyVO();
        if (!bannerFiles.isEmpty()) {
            learningModifyVO.setBannerFile(bannerFiles.get(0));
        }

        List<VideoFileInfo> videoFileInfoList = new ArrayList<>();
        for (AttachmentFile attachmentFile : videoFiles) {
            String originalFileName = attachmentFile.getOriginalFileName();
            String[] split = originalFileName.split("_");
            videoFileInfoList.add(VideoFileInfo.builder()
                    .videoSrc(attachmentFile.getFullPath(""))
                    .videoName(originalFileName)
                    .order(split[0])
                    .contents(split[1])
                    .build());
        }

        learningModifyVO.setVideoFiles(videoFileInfoList);
        learningModifyVO.setLearning(learning);
        return learningModifyVO;
    }

    /**
     * 강의 단건 조회 By Id With Question
     */
    @Override
    public Learning getLearningByIdWithQuestion(Long learningId) {
        return learningRepository.findLearningByIdWithQuestions(learningId);
    }

    /**
     * Get 강의 상세. By View
     */
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

    /**
     * 강의 학습 페이지 상세. By View
     */
    @Override
    public LearningListenVO getLearningListen(Long accountId, Long learningId) {
        // 해당 유저가 구매이력에 해당 강의를 가지고 있는지 검증
        accountService.chkAccountHasLearning(accountId, learningId);

        Learning learning = this.getLearningById(learningId);
        AttachmentGroup attachmentGroup = learning.getAttachmentGroup();

        // 해당 강의 조회해서 비디오 파일들 가져오기
        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.VIDEO);

        if (attachmentFiles.isEmpty()) {
            throw new IllegalArgumentException("접근할 영상이 없음.");
        }

        // 비디오 타이틀을 목록화해서 보여줘야함
        Map<String, Long> videoFileIdMap = new HashMap<>();
        Set<String> videoTitleSet = new HashSet<>();

        for (AttachmentFile attachmentFile : attachmentFiles) {
            String originalFileName = attachmentFile.getOriginalFileName();
            videoTitleSet.add(originalFileName);
            videoFileIdMap.put(originalFileName, attachmentFile.getId());
        }

        AttachmentFile attachmentFile = attachmentFiles.get(0);
        return LearningListenVO.builder()
                .learning(learning)
                .videoSrc(attachmentFile.getFullPath(""))
                .videoTitleSet(videoTitleSet)
                .videoFileIdMap(videoFileIdMap)
                .playingVideo(attachmentFile.getOriginalFileName())
                .build();
    }

    // 강의 활성화
    @Override
    @Transactional
    public void modOpeningLearning(Long accountId, Long learningId) {
        ProfessionalAccount professionalAccount = professionalService.getProfessionalByAccountId(accountId);
        Learning learning = this.getLearningById(learningId);

        if (!professionalAccount.getLearningSet().contains(learning)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        learning.setOpening(!learning.isOpening());
        learning.setOpeningDate(LocalDateTime.now());

        if (learning.isOpening()) applicationEventPublisher.publishEvent(new LearningCreateEvent(learning));
        else applicationEventPublisher.publishEvent(new LearningClosedEvent(learning));
    }

    // 강의 정보 수정.
    @Override
    @Transactional
    public Learning modLearningContext(ModLearningVO modLearningVO, Long accountId, Long learningId) {
        // 기본 정보들만 수정됨.
        Learning learning = this.getLearningWithTagsById(learningId);

        learning.setTitle(modLearningVO.getTitle());
        learning.setPrice(modLearningVO.getPrice());
        learning.setSubscription(modLearningVO.getSubscription());
        learning.setSimpleSubscription(modLearningVO.getSimpleSubscription());
        learning.setMainCategory(modLearningVO.getMainCategory());

        List<String> inputTagTitleList = new ArrayList<>();
        try {
            // 태그 입력값 분석
            List<TagBinder> tagBinderList = objectMapper.readValue(modLearningVO.getSkills(), new TypeReference<>() {});
            inputTagTitleList.addAll(tagBinderList.stream().map(TagBinder::getValue).collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(modLearningVO.getSkills());
        }

        Set<LearningTagJoinTable> tags = new HashSet<>(learning.getTags());
        for (LearningTagJoinTable learningTagJoinTable : tags) {
            String title = learningTagJoinTable.getTag().getTitle();

            // 필요없는 태그들은 삭제.
            if (!inputTagTitleList.contains(title)) {
                inputTagTitleList.remove(title);
                learning.getTags().remove(learningTagJoinTable);
            }
        }

        // 기존에 없던 태그들.
        for (String tagTitle : inputTagTitleList) {
            Tag tag = tagService.regTag(RegTagVO.builder()
                    .title(tagTitle)
                    .build());

            LearningTagJoinTable learningTagJoinTable = learningTagService.regLearningTagJointable(learning, tag);
            learning.setTags(learningTagJoinTable);
        }

        // 강의가 공개되었다면, 이벤트 발생.
        if (learning.isOpening()) {
            applicationEventPublisher.publishEvent(new LearningUpdateEvent(learning));
        }

        return learning;
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
    public Page<Learning> getPagingByCategory(String category, Pageable pageable) {
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
        Page<Learning> learningPage;
        if (StringUtils.isNullOrEmpty(subCategory)) {
            learningPage = this.getPagingByCategory(mainCategory, pageable);
        } else {
            learningPage = this.getPagingByCategoryAndKeyword(mainCategory, subCategory, pageable);
        }

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

        Paging paging = Paging.builder()
                .hasNext(learningPage.hasNext())
                .hasPrevious(learningPage.hasPrevious())
                .number(learningPage.getNumber())
                .totalPages(Math.max(learningPage.getTotalPages() - 1, 0))
                .paginationUrl(defaultButtonUrlBuilder)
                .build();

        return LearningListVO.builder()
                .learningList(learningPage.getContent())
                .learningImageMap(learningImageMap)
                .paging(paging)
                .build();
    }

    /**
     * Get Learning 목록. By View With keyword Learning Of API
     */
    @Override
    public LearningListAPIVO getLearningListByViewWithKeywordOfAPI(String mainCategory, String subCategory, Pageable pageable) {
        // Learning 페이징
        Page<Learning> learningPage;
        if (StringUtils.isNullOrEmpty(subCategory)) {
            learningPage = this.getPagingByCategory(mainCategory, pageable);
        } else {
            learningPage = this.getPagingByCategoryAndKeyword(mainCategory, subCategory, pageable);
        }

        List<Learning> learningList = learningPage.getContent();

        // 강의 이미지 목록 조회
        List<LearningAPIVO> resultList = new ArrayList<>();
        for (Learning learning : learningList) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
            if (attachmentFiles.size() != 1) {
                throw new IllegalArgumentException();
            }

            AttachmentFile attachmentFile = attachmentFiles.get(0);

            Set<String> tagList = new HashSet<>();
            for (LearningTagJoinTable learningTagJoinTable : learning.getTags()) {
                tagList.add(learningTagJoinTable.getTag().getTitle());
            }

            resultList.add(
                    LearningAPIVO.builder()
                            .learningId(learning.getId())
                            .imageSrc(attachmentFile.getFullPath(""))
                            .imageName(attachmentFile.getOriginalFileName())
                            .title(learning.getTitle())
                            .tagTitleList(tagList)
                            .rating(learning.getRating())
                            .price(learning.getPrice())
                            .openingDate(learning.getOpeningDate())
                            .build()
            );
        }

        String sortProperty = pageable.getSort().toString().contains("openingDate") ? "openingDate" : "rating";

        // 페이지네이션 기본 url
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/learning/search/");
        stringBuilder.append(mainCategory);
        if (StringUtils.isNotNullAndEmpty(subCategory)) {
            stringBuilder.append("?keyword=");
            stringBuilder.append(subCategory);
        }
        stringBuilder.append("&sort=");
        stringBuilder.append(sortProperty);
        stringBuilder.append(",desc&page=");

        Paging paging = Paging.builder()
                .hasNext(learningPage.hasNext())
                .hasPrevious(learningPage.hasPrevious())
                .number(learningPage.getNumber())
                .totalPages(Math.max(learningPage.getTotalPages() - 1, 0))
                .paginationUrl(stringBuilder.toString())
                .build();

        return LearningListAPIVO.builder()
                .learningList(resultList)
                .paging(paging)
                .build();
    }

    /**
     * Del Learning 목록. By 강의 제공자 Id
     */
    @Override
    @Transactional
    public void delLearningByProfessionalId(Long professionalId) {
        learningRepository.delByProfessionalId(professionalId, LocalDateTime.now());
    }

    /**
     * mod Learning 활성화. By 강의 제공자 Id
     */
    @Override
    @Transactional
    public void modLearningByProfessionalId(Long professionalId) {
        learningRepository.modByProfessionalId(professionalId, LocalDateTime.now());
    }

    /**
     * 강의 배너 이미지 수정
     */
    @Override
    @Transactional
    public void modLearningBannerImage(Long learningId, MultipartFile bannerFile) {
        Learning learning = this.getLearningById(learningId);
        AttachmentGroup attachmentGroup = learning.getAttachmentGroup();
        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.BANNER);

        // 배너 이미지 존재하면 일단 지움
        for (AttachmentFile attachmentFile : attachmentFiles) {
            attachmentService.delAttachmentFile(attachmentFile.getId());
        }

        attachmentService.saveFile(attachmentGroup, bannerFile, AttachmentFileType.BANNER);
    }

    /**
     * 강의 영상 비디오 수정
     */
    @Override
    @Transactional
    public void modLearningVideo(Long learningId, String title, Integer order, MultipartFile videoFile) {
        Learning learning = this.getLearningById(learningId);
        AttachmentGroup attachmentGroup = learning.getAttachmentGroup();
        List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(attachmentGroup.getId(), AttachmentFileType.VIDEO);

        // 파일 제목에 order 를 붙힌 값이 존재할 경우 그 값만 삭제.
        for (AttachmentFile attachmentFile : attachmentFiles) {
            String originalFileName = attachmentFile.getOriginalFileName();
            String[] split = originalFileName.split("_");

            if (Integer.valueOf(split[0]).equals(order)) {
                attachmentService.delAttachmentFile(attachmentFile.getId());
            }
        }

        attachmentService.saveFile(attachmentGroup, videoFile, AttachmentFileType.VIDEO, order + "_" + videoFile.getOriginalFilename());
    }

    // ====== web 모듈 서비스 ======

    /**
     * Get Learning Paging. With keyword Learning For API
     */
    @Override
    public Page<LearningVO> getLearningPagingWithKeywordForAPI(String mainCategory, String subCategory, Pageable pageable) {
        mainCategory = mainCategory.equals("all") ? "" : mainCategory;
        if (StringUtils.isNullOrEmpty(subCategory)) {
            return learningRepository.findPagingByCategory(pageable, mainCategory);
        } else {
            return learningRepository.findPagingByCategoryAndKeyword(pageable, mainCategory, subCategory);
        }
    }
}
