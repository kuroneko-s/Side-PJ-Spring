package com.choidh.web.learning.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.question.entity.Question;
import com.choidh.service.question.repository.QuestionRepository;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.repository.TagRepository;
import com.choidh.service.video.entity.Video;
import com.choidh.service.video.repository.VideoRepository;
import com.choidh.web.account.service.AccountServiceImpl;
import com.choidh.web.kakao.vo.KakaoPayForm;
import com.choidh.web.learning.service.LearningService;
import com.choidh.web.learning.validator.LearningValidator;
import com.choidh.web.learning.vo.LearningFormVO;
import com.choidh.web.question.service.QuestionService;
import com.choidh.web.question.vo.QuestionForm;
import com.choidh.web.review.service.ReviewService;
import com.choidh.web.review.vo.ReviewVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.tag.vo.TagForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.*;

@Controller
@RequiredArgsConstructor
public class LearningController {
    private final LearningService learningService;
    private final LearningValidator learningValidator;
    private final LearningRepository learningRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final VideoRepository videoRepository;
    private final AccountServiceImpl accountServiceImpl;
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final ReviewService reviewService;

    @InitBinder("learningFormVO")
    private void initVideoForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(learningValidator);
    }

    // 업로드 페이지
    @GetMapping("/profile/learning/create")
    public String viewUpload(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new LearningFormVO());

        return CREATE_LEARNING;
    }

    // 강의 업로드 처리
    @PostMapping("/profile/learning/create")
    public String createLearning(@CurrentAccount Account account, Model model,
                                 @Valid LearningFormVO learningFormVO, Errors errors, RedirectAttributes attributes) {
        Account getAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage((account.getId()))));

        if (errors.hasErrors()) {
            model.addAttribute("account", getAccount);
            model.addAttribute("message", "잘못 입력 하셨습니다.");

            return CREATE_LEARNING;
        }

        Learning learning = learningService.saveLearning(learningFormVO, getAccount);

        attributes.addFlashAttribute("account", getAccount);
        attributes.addFlashAttribute("message", learning.getTitle() + " 강의가 추가되었습니다");

        return "redirect:/profile/learning/create";
    }

    @GetMapping("/profile/learning/list")
    public String viewLearningList(@CurrentAccount Account account, Model model) {
        Account newAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(account.getId())));

        List<Learning> learningList = learningRepository.findAllByAccountOrderByCreateLearningDesc(account);

        model.addAttribute("account", newAccount);
        model.addAttribute("learningList", learningList);
        
        return "learning/learning_list";
    }

    @GetMapping("/profile/learning/upload/{id}")
    public String viewVideoUpdate(@CurrentAccount Account account, Model model, @PathVariable Long id) throws JsonProcessingException {
        Learning learning = learningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));
        List<String> tagList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("tags", learning.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));

        return "learning/learning_upload";
    }

    @PostMapping("/profile/learning/upload/{id}/add")
    public @ResponseBody ResponseEntity learningAddTags(@CurrentAccount Account account, @PathVariable Long id, @RequestBody TagForm tagForm) {
        Optional<Learning> learning = learningRepository.findById(id);
        if (learning.isEmpty()) return ResponseEntity.badRequest().build();

        Tag tag = tagRepository.findByTitle(tagForm.getTitle());
        if (tag == null) tag = Tag.builder().title(tagForm.getTitle()).build();

        learningService.saveLearningTags(learning.orElseThrow(), tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile/learning/upload/{id}/remove")
    public @ResponseBody ResponseEntity learningRemoveTags(@CurrentAccount Account account, @PathVariable Long id, @RequestBody TagForm tagForm) {
        Optional<Learning> learning = learningRepository.findById(id);
        Tag tag = tagRepository.findByTitle(tagForm.getTitle());

        if (learning.isEmpty() || tag == null) return ResponseEntity.badRequest().build();

        learningService.removeLearningTags(learning.orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id))), tagForm);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/profile/learning/upload/{id}/video", produces="text/plain;Charset=UTF-8")
    public @ResponseBody ResponseEntity videoUpdate(@CurrentAccount Account account, @PathVariable Long id,
                                                    MultipartHttpServletRequest httpServletRequest) {
        List<MultipartFile> videofile = httpServletRequest.getFiles("videofile");
        if (videofile.isEmpty()) return ResponseEntity.badRequest().build();

        Learning learning = learningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));

        try {
            learningService.saveVideo(videofile, account, learning);

            return ResponseEntity.ok().build();
        }catch (IOException e){
            e.printStackTrace();

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/profile/learning/upload/{id}/banner", produces="text/plain;Charset=UTF-8")
    @ResponseBody
    public ResponseEntity bannerUpdate(@CurrentAccount Account account, Model model, @PathVariable Long id,
                                     @RequestParam("banner") MultipartFile multipartFile) {
        Learning learning = learningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));

        if (multipartFile.isEmpty()) return ResponseEntity.badRequest().build();

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null) return ResponseEntity.badRequest().build();

        int i = originalFilename.indexOf(".");
        String extension = originalFilename.substring(i + 1);
        if (isNotImageFile(extension)) return ResponseEntity.badRequest().build();

        try {
            learningService.saveBanner(multipartFile, account, learning);

            return ResponseEntity.ok().build();
        }catch (IOException e){
            e.printStackTrace();

            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/learning/{id}")
    public String viewMainLearning(@CurrentAccount Account account, Model model, @PathVariable Long id){
        Account newAccount = accountRepository.findAccountWithLearnings(account.getId()); //account 조회
        Learning learning = learningRepository.findLearningWithVideosAndTagsAndReviewsAndQuestionsById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id))); //learning 조회 tags, reviews, questions, videos
        boolean contains = newAccount.getLearnings().contains(learning);
        List<String> contentsTitle = learningService.getContentsTitle(learning);

        model.addAttribute("account", newAccount);
        model.addAttribute("learning", learning);
        model.addAttribute("learnings", contains);
        model.addAttribute("listenLearning", newAccount.getListenLearning().contains(learning));
        model.addAttribute("countVideo", learning.getVideoCount());
        model.addAttribute("tags", learning.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("ratings", learning.getRating_int());
        model.addAttribute("halfrating", learning.checkRating_boolean());
        model.addAttribute("rating", learning.emptyRating());
        model.addAttribute("learningRating", learning.getRating());
        model.addAttribute("canOpen", learningService.checkOpenTimer(learning.isStartingLearning(), learning.isClosedLearning(), contains));
        model.addAttribute("canClose", learningService.checkCloseTimer(learning.isStartingLearning(), learning.isClosedLearning(), contains));
        model.addAttribute("canCloseTimer", learning.getCloseLearning() == null || learning.getCloseLearning().isBefore(LocalDateTime.now().minusMinutes(30)));
        model.addAttribute("canOpenTimer", learning.getOpenLearning() == null || learning.getOpenLearning().isBefore(LocalDateTime.now().minusMinutes(30)));
        model.addAttribute("contentsTitle", contentsTitle);
        model.addAttribute("reviews", learning.getReviews());
        model.addAttribute("questions", learning.getQuestions());

        return "learning/main_learning";
    }

    @GetMapping("/profile/learning/update/{id}")
    public String viewUpdateMainLearning(@CurrentAccount Account account, Model model, @PathVariable Long id) throws JsonProcessingException {
        Learning learning = learningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));
        List<String> tagList = learning.getTags()
                .stream().map(Tag::getTitle)
                .collect(Collectors.toList());

        LearningFormVO learningFormVO = new LearningFormVO();
        learningFormVO.setTitle(learning.getTitle());
        learningFormVO.setLecturerName(learning.getLecturerName());
        learningFormVO.setLecturerDescription(learning.getLecturerDescription());
        learningFormVO.setPrice(learning.getPrice());
        learningFormVO.setKategorie(learning.getKategorie());
        learningFormVO.setSimplesubscription(learning.getSimpleSubscription());

        model.addAttribute(account);
        model.addAttribute("learning", learning);
        model.addAttribute("learningFormVO", learningFormVO);
        model.addAttribute("tags", learning.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));

        return "learning/update_learning";
    }

    @GetMapping("/profile/learning/video/update/{learningId}")
    public String updateVideoLearning(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long id) {
        Learning learning = learningRepository.findLearningWithVideosById(id).orElseThrow();
        Set<Video> videos = learning.getVideos();

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("videoList", videos);

        return "learning/update_learning_video";
    }

    @PostMapping("/profile/video/{learningId}/remove/{videoId}")
    public String removeVideo(@CurrentAccount Account account, RedirectAttributes attributes,
                              @PathVariable("learningId") Long learningId, @PathVariable("videoId") Long videoId) {
        Learning learning = learningRepository.findLearningWithVideosById(learningId)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(learningId)));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException(getVideoNotFoundErrorMessage(videoId)));

        Learning newLearning = learningService.removeVideo(learning, video, account);

        attributes.addFlashAttribute("account", account);

        return "redirect:/profile/learning/video/update/" + newLearning.getId();
    }

    @PostMapping("/profile/learning/update/{id}/script")
    public String updateLearningScript(@CurrentAccount Account account, Model model, @PathVariable Long id,
                                       @Valid LearningFormVO learningFormVO, Errors errors,
                                       RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            attributes.addFlashAttribute("message", "값이 잘못되었습니다.");

            return "redirect:/profile/learning/update/" + id;
        }

        learningService.updateLearningScript(learningFormVO, account, id);

        attributes.addFlashAttribute("message", "수정되었습니다.");
        return "redirect:/profile/learning/update/" + id;
    }

    @GetMapping("/profile/learning/start/{id}")
    public String startLearning(@CurrentAccount Account account, Model model, @PathVariable Long id, RedirectAttributes attributes) {
        learningService.startLearning(id);

        attributes.addFlashAttribute("message", "강의가 오픈되었습니다.");
        return "redirect:/learning/" + id;
    }

    @GetMapping("/profile/learning/close/{id}")
    public String closedLearning(@CurrentAccount Account account, Model model, @PathVariable Long id, RedirectAttributes attributes) {
        learningService.closeLearning(id);

        attributes.addFlashAttribute("message", "강의가 닫혔습니다.");
        return "redirect:/learning/" + id;
    }

    //학습하기 버튼
    @GetMapping("/learning/{id}/listen")
    public String listenLearning(@CurrentAccount Account account, Model model, @PathVariable Long id) {
        Learning learning = learningRepository.findLearningWithVideosById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));
        List<String> contentsTitle = learningService.getContentsTitle(learning).stream().sorted().collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("contentsList", contentsTitle);
        model.addAttribute("now", contentsTitle.get(0));

        return "learning/listen_learning";
    }

    @GetMapping("/learning/listen/{learningId}/{title}")
    public String listenLearningPlayVideo(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long id,
                                          @PathVariable("title") String title, RedirectAttributes attributes) {
        Learning learning = learningRepository.findLearningWithVideosById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));
        List<String> contentsTitle = learningService.getContentsTitle(learning).stream().sorted().collect(Collectors.toList());
        List<Video> videos = videoRepository.findByTitleAndLearning(title, learning);
        final String videoPath = "/video/" + account.getId() + "/" + learning.getId() + "/" + videos.get(0).getVideoTitle();

        if(videos.size() >= 2){
            attributes.addFlashAttribute("message", "잘못된 요청입니다.");
            return "redirect:/learning/" + learning.getId() + "/listen";
        }

        model.addAttribute("account", account);
        model.addAttribute("learning", learning);
        model.addAttribute("contentsList", contentsTitle);
        model.addAttribute("now", videos.get(0).getVideoTitle());
        model.addAttribute("videoPath", videoPath);

        return "learning/listen_learning";
    }

    @GetMapping("/question/{id}")
    public String showQuestionPopup(@CurrentAccount Account account, Model model, @PathVariable Long id){
        Learning learning = learningRepository.findById(id).orElseThrow();

        model.addAttribute("learning", learning);
        model.addAttribute("account", account);
        model.addAttribute(new QuestionForm());

        return "question";
    }

    @PostMapping("/question/{id}")
    public String saveQuestionPopup(@CurrentAccount Account account, RedirectAttributes attributes, @PathVariable Long id, QuestionForm questionForm){
        Learning learning = learningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));

        Account newAccount = learningService.saveQuestion(modelMapper.map(questionForm, Question.class), account, learning);

        attributes.addFlashAttribute("account", newAccount);

        return "redirect:/learning/" + learning.getId();
    }

    /**
     * 리뷰 페이지
     */
    @GetMapping("/review/{id}")
    public String showReviewPopup(@CurrentAccount Account account, Model model, @PathVariable Long id){
        Learning learning = learningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));

        model.addAttribute("learning", learning);
        model.addAttribute("account", account);
        model.addAttribute(new ReviewVO());

        return "review";
    }

    /**
     * 리뷰 추가.
     */
    @PostMapping("/review/{id}")
    public String saveReview(@CurrentAccount Account account, @PathVariable Long id, Model model,
                             @Valid ReviewVO reviewVO, Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("message", "잘못입력하셨습니다!");

            return "review";
        }

        // 리뷰 추가.
        Account newAccount = reviewService.saveReview(reviewVO, account.getId(), id);

        attributes.addFlashAttribute("account", newAccount);

        return "redirect:/learning/" + id;
    }

    @GetMapping("/learning/{learningId}/buy")
    public String viewBuyLearning(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long id) {
        Account newAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(account.getId())));
        Learning learning = learningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));
        accountServiceImpl.addLearningInCart(newAccount, learning);

        Set<Learning> cartList = newAccount.getCartList();
        model.addAttribute("account", newAccount);
        model.addAttribute("learningList", cartList);
        model.addAttribute("totalPrice", cartList.stream().mapToInt(Learning::getPrice).sum());
        model.addAttribute(new KakaoPayForm());

        return "shop/buy";
    }

    @GetMapping("/learning/buy")
    public String viewBuyCartList(@CurrentAccount Account account, Model model) {
        Set<Learning> cartList = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(account.getId())))
                .getCartList();

        model.addAttribute("account", account);
        model.addAttribute("learningList", cartList);
        model.addAttribute("totalPrice", cartList.stream().mapToInt(Learning::getPrice).sum());
        model.addAttribute(new KakaoPayForm());

        return "shop/buy";
    }

    @GetMapping("/learning/{learningId}/cart/add")
    @ResponseBody
    public ResponseEntity cartLearning(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long id) {
        Account newAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(account.getId())));
        Learning learning = learningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id)));

        accountServiceImpl.addLearningInCart(newAccount, learning);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/learning/question/{learningId}")
    public String viewLearningQuestion(@CurrentAccount Account account, Model model, @PathVariable("learningId") Long id){
        Account newAccount = accountRepository.findAccountWithTags(account.getId());
        Learning learning = learningRepository.findLearningWithQuestionsAndTagsById(id)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(id))); //questions, tags
        ArrayList<Question> questions = new ArrayList<>(learning.getQuestions());

        model.addAttribute(newAccount);
        model.addAttribute(new QuestionForm());
        model.addAttribute("countVideo", learning.getVideoCount());
        model.addAttribute("learning", learning);
        model.addAttribute("tags", learning.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("ratings", learning.getRating_int());
        model.addAttribute("halfrating", learning.checkRating_boolean());
        model.addAttribute("rating", learning.emptyRating());
        model.addAttribute("learningRating", learning.getRating());
        model.addAttribute("questions", questions);
        model.addAttribute("idList", questions.stream().map(Question::getId).collect(Collectors.toList()));

        return "learning/question";
    }

    @PostMapping("/learning/question/{questionId}/update")
    public String updateQuestionAnswer(@CurrentAccount Account account, RedirectAttributes attributes,
                                 @PathVariable("questionId") Long id ,QuestionForm questionForm) {

        Question question = questionService.updateQuestion(questionForm, id);
        attributes.addFlashAttribute("account", account);
        Learning learning = question.getLearning();

        return "redirect:/learning/question/" + learning.getId();
    }

    @PostMapping("/learning/question/{questionId}/create")
    public String createQuestionAnswer(@CurrentAccount Account account, RedirectAttributes attributes,
                                 @PathVariable("questionId") Long id ,QuestionForm questionForm) {

        Question question = questionService.updateQuestion(questionForm, id);
        attributes.addFlashAttribute("account", account);
        Learning learning = question.getLearning();

        return "redirect:/learning/question/" + learning.getId();
    }

    @GetMapping("/learning/question/{questionId}/delete")
    public String deleteQuestionAnswer(@CurrentAccount Account account, RedirectAttributes attributes,
                                       @PathVariable("questionId") Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getQuestionNotFoundErrorMessage(id)));

        if (!question.getAccount().getId().equals(account.getId())) {
            attributes.addFlashAttribute("message", "잘못된 요청입니다");

            return "redirect:/learning/question/" + question.getLearning().getId();
        }

        questionService.deleteQuestion(question);
        attributes.addFlashAttribute("account", account);

        return "redirect:/learning/question/" + question.getLearning().getId();
    }
}
