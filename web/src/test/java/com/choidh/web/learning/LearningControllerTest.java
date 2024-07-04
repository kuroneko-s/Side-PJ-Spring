package com.choidh.web.learning;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.service.AccountServiceImpl;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.learning.vo.RegLearningVO;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.repository.TagRepository;
import com.choidh.web.config.WithAccount;
import com.choidh.web.learning.vo.LearningFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.choidh.service.common.vo.AppConstant.CREATE_LEARNING;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class LearningControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LearningRepository learningRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LearningService learningService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private AccountServiceImpl accountServiceImpl;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    @DisplayName("강의 만들기 뷰")
    @WithAccount("test@naver.com")
    public void viewUploader() throws Exception {
        mockMvc.perform(get("/profile/learning/create"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learningFormVO"))
                .andExpect(view().name(CREATE_LEARNING))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("강의 만들기 - 성공")
    @WithAccount("test@naver.com")
    public void postLearning() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String title = "테스트_1";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "흑우냥이";
        String lectureSubscription = "게시자에 대한 설명입니다.";
        String simpleSubscription = "간단 설명입니다.";
        int price = 100000;
        String kategorie = "1"; // 1 - web, 2 - 알고리즘

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("simplesubscription", simpleSubscription)
                .param("price", price + "")
                .param("kategorie", "1")
                .param("subscription", subscription)
                .param("lecturerName", lecturerName)
                .param("lecturerDescription", lectureSubscription)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/profile/learning/create"))
                .andExpect(status().is3xxRedirection());

//        assertNotNull(learning);
//        assertEquals(learning.getTitle(), title);
//        assertEquals(learning.getSubscription(), subscription);
//        assertEquals(learning.getLecturerName(), lecturerName);
//        assertEquals(learning.getLecturerDescription(), lectureSubscription);
//
//        assertTrue(account.getLearnings().contains(learning));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 만들기 - 실패_lecturerDescription null")
    public void postLearning_fail_lecturerDescription() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String title = "";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "test";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", "Test_Title")
                .param("subscription", subscription)
                .param("lecturerName", lecturerName)
                .with(csrf()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("learningFormVO"))
                .andExpect(model().hasErrors())
                .andExpect(view().name(CREATE_LEARNING))
                .andExpect(status().isOk());

//        assertNull(learning);
    }
    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 만들기 - 실패_title null")
    public void postLearning_fail_title() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String title = "";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "test";
        String lectureSubscription = "게시자에 대한 설명입니다.";

        mockMvc.perform(post("/profile/learning/create")
                .param("subscription", subscription)
                .param("lecturerName", lecturerName)
                .param("lecturerDescription", lectureSubscription)
                .with(csrf()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("learningFormVO"))
                .andExpect(model().hasErrors())
                .andExpect(view().name(CREATE_LEARNING))
                .andExpect(status().isOk());

//        assertNull(learning);
    }
    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 만들기 - 실패_subscription null")
    public void postLearning_fail_subscription() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String title = "테스트_1";
        String subscription = "";
        String lecturerName = "흑우냥이_1";
        String lectureSubscription = "게시자에 대한 설명입니다.";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("lecturerName", lecturerName)
                .param("lecturerDescription", lectureSubscription)
                .with(csrf()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("learningFormVO"))
                .andExpect(model().hasErrors())
                .andExpect(view().name(CREATE_LEARNING))
                .andExpect(status().isOk());

//        assertNull(learning);
    }


    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 만들기 - 실패_lecturerName null")
    public void postLearning_fail_lecturerName() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String title = "테스트_1";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "";
        String lectureSubscription = "게시자에 대한 설명입니다.";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("subscription", subscription)
                .param("lecturerDescription", lectureSubscription)
                .with(csrf()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("learningFormVO"))
                .andExpect(model().hasErrors())
                .andExpect(view().name(CREATE_LEARNING))
                .andExpect(status().isOk());

//        assertNull(learning);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 리스트 뷰")
    public void updateLearning() throws Exception {
        mockMvc.perform(get("/profile/learning/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("learning/learning_list"));
    }

    private Learning postLearning(Account account) {
        LearningFormVO learningFormVO = new LearningFormVO();
        learningFormVO.setTitle("테스트_1");
        learningFormVO.setSubscription("테스트_1 설명입니다.");
        learningFormVO.setSimplesubscription("테스트_ 심플 설명입니다.");
        learningFormVO.setPrice(10000);
        learningFormVO.setKategorie("알고리즘");

        return learningService.regLearning(modelMapper.map(learningFormVO, RegLearningVO.class), account.getId());
    }

    private void createTags(Account account) {
        Tag tag_1 = tagRepository.save(Tag.builder().title("test_Tag_1").build());
        Tag tag_2 = tagRepository.save(Tag.builder().title("test_Tag_2").build());
//        accountServiceImpl.addTag(account, tag_1);
//        accountServiceImpl.addTag(account, tag_2);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 영상 업로드 뷰 - 성공")
    public void viewVideosUpdate() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        Learning learning = postLearning(account);
        createTags(account);
        List<Tag> all = tagRepository.findAll();
        for (Tag tag : all) {
            // learning.getTags().add(tag);
        }

        mockMvc.perform(get("/profile/learning/upload/" + learning.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("learning/learning_upload"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 태그 추가 - 성공")
    public void learningAddTags_success() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        mockMvc.perform(post("/profile/learning/upload/" + learning.getId() + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("addTags_test_1"))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("addTags_test_1");

        assertTrue(learning.getTags().contains(newTag));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 태그 삭제 - 성공")
    public void learningRemoveTags_success() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        Learning learning = postLearning(account);
        createTags(account);
        Tag learningTag = tagRepository.findByTitle("test_Tag_1");
        // learning.getTags().add(learningTag);

        mockMvc.perform(post("/profile/learning/upload/" + learning.getId() + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("test_Tag_1"))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("test_Tag_1");

        assertNotNull(newTag);
        assertFalse(learning.getTags().contains(newTag));
        assertFalse(learning.getTags().contains(learningTag));
        assertEquals(learningTag, newTag);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 태그 삭제 - 실패_not found tag")
    public void learningRemoveTags_fail() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        Learning learning = postLearning(account);
        createTags(account);
        Tag learningTag = tagRepository.findByTitle("test_Tag_1");
        // learning.getTags().add(learningTag);

        mockMvc.perform(post("/profile/learning/upload/" + learning.getId() + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("test_Tag_3"))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        Tag newTag = tagRepository.findByTitle("test_Tag_3");

        assertNull(newTag);
        assertTrue(learning.getTags().contains(learningTag));
    }

    /*@Test
    @WithAccount("test@naver.com")
    @DisplayName("동영상 업로드 - 성공")
    public void videoUpdate_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        // TODO: 테스트 실패가 지금 영상 관련 관리하는 방식을 변경하려는데 그거랑 현재 코드랑 상충되서 에러나는중임. 일단 전체적으로 구조만 수정하고 내부 동작 코드는 나중에 수정할 예정임.
        MockMultipartFile file_1 = new MockMultipartFile("videofile", "test_movie-1.mov", "text/plain", "movie1 data".getBytes());
        MockMultipartFile file_2 = new MockMultipartFile("videofile", "test_movie-2.mov", "text/plain", "movie2 data".getBytes());
        MockMultipartFile file_3 = new MockMultipartFile("videofile", "test_movie-3.mov", "text/plain", "movie3 data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/video")
                .file(file_1)
                .file(file_2)
                .file(file_3)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        List<String> videos = learning.getVideos().stream().map(Video::getVideoTitle).collect(Collectors.toList());

        assertTrue(learning.getVideoCount() > 0);
        assertEquals(videos.size(), 3);
        assertNotNull(learning.getUploadVideo());

        log.info("===========info");

        for (int i = 0; i < videos.size(); i++) {
            log.info(videos.get(i));
            log.info(i + "");
        }
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("동영상 업로드 - 실패_values null")
    public void videoUpdate_fail() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/video")
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        List<String> videos = learning.getVideos().stream().map(Video::getVideoTitle).collect(Collectors.toList());

        assertEquals(learning.getVideoCount(), 0);
        assertTrue(videos.isEmpty());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("배너 이미지 업로드 - 성공")
    public void videoBanner_success() throws Exception {
        // TODO : 배너도 영상과 비슷한 이유로 동작 안하고 있을것임. 추후 수정.
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        MockMultipartFile file_png = new MockMultipartFile("banner", "test_movie_1.png", "text/plain", "movie1 data".getBytes());
        MockMultipartFile file_jpg = new MockMultipartFile("banner", "test_movie_1.jpg", "text/plain", "movie1 data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/banner")
                .file(file_png)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        assertNotNull(learning.getBannerServerPath());
        assertNotNull(learning.getBannerBytes());

        log.info("===============info");
        log.info(learning.getBannerServerPath());
        log.info(learning.getBannerBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/banner")
                .file(file_jpg)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        assertNotNull(learning.getBannerServerPath());
        assertNotNull(learning.getBannerBytes());

        log.info("===============info");
        log.info(learning.getBannerServerPath());
        log.info(learning.getBannerBytes());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("배너 이미지 업로드 - 실패_values null")
    public void videoBanner_fail() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        MockMultipartFile file_png = new MockMultipartFile("banner", "test_movie_1.mp3", "text/plain", "movie1 data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/banner")
                .file(file_png)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        assertNull(learning.getBannerServerPath());
        assertNull(learning.getBannerBytes());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 상세 페이지 뷰")
    public void getLearningDetailView() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        mockMvc.perform(get("/learning/" + learning.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("learnings"))
                .andExpect(model().attributeExists("listenLearning"))
                .andExpect(model().attributeExists("countVideo"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("ratings"))
                .andExpect(model().attributeExists("halfrating"))
                .andExpect(model().attributeExists("rating"))
                .andExpect(model().attributeExists("learningRating"))
                .andExpect(model().attributeExists("canOpen"))
                .andExpect(model().attributeExists("canClose"))
                .andExpect(model().attributeExists("canCloseTimer"))
                .andExpect(model().attributeExists("canOpenTimer"))
                .andExpect(model().attributeExists("contentsTitle"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attributeExists("questions"))
                .andExpect(view().name("learning/main_learning"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 페이지에서 편집 화면으로 이동")
    public void updateMainLearning() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        mockMvc.perform(get("/profile/learning/update/" + learning.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("learningFormVO"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(status().isOk())
                .andExpect(view().name("learning/update_learning"));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 성공")
    public void updateLearningScript_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("title", learning.getTitle())
                .param("simplesubscription", learning.getSimpleSubscription())
                .param("subscription", "테스트_1 수정 설명입니다.")
                .param("lecturerName", "mark.2_흑우냥이")
                .param("lecturerDescription", "테스트_게시자_수정_입니다.")
                .param("price", "1202139421")
                .param("kategorie", "1")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        Learning newLearning = learningRepository.findById(learning.getId()).orElseThrow();

        assertEquals(newLearning.getSubscription(), "테스트_1 수정 설명입니다.");
        assertEquals(newLearning.getLecturerName(), "mark.2_흑우냥이");
        assertEquals(newLearning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 실패 title null")
    public void updateLearningScript_success_title_null() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("subscription", "테스트_1 수정 설명입니다.")
                .param("lecturerName", "mark.2_흑우냥이")
                .param("lecturerDescription", "테스트_게시자_수정_입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertNotEquals(learning.getSubscription(), "테스트_1 수정 설명입니다.");
        assertNotEquals(learning.getLecturerName(), "mark.2_흑우냥이");
        assertNotEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 실패 subscription null")
    public void updateLearningScript_success_subscription_null() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("title", "테스트_2")
                .param("lecturerName", "mark.2_흑우냥이")
                .param("lecturerDescription", "테스트_게시자_수정_입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertNotEquals(learning.getTitle(), "테스트_2");
        assertNotEquals(learning.getLecturerName(), "mark.2_흑우냥이");
        assertNotEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 실패 lecturerDescription null")
    public void updateLearningScript_success_lecturerDescription_null() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("title", "테스트_2")
                .param("lecturerName", "mark.2_흑우냥이")
                .param("subscription", "테스트_1 수정 설명입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertNotEquals(learning.getTitle(), "테스트_2");
        assertNotEquals(learning.getLecturerName(), "mark.2_흑우냥이");
        assertNotEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 실패 lecturerName null")
    public void updateLearningScript_success_lecturerName_null() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("title", "테스트_2")
                .param("subscription", "테스트_2 수정 설명코드입니다.")
                .param("lecturerDescription", "테스트_게시자_수정_입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertNotEquals(learning.getTitle(), "테스트_2");
        assertNotEquals(learning.getSubscription(), "테스트_2 수정 설명코드입니다.");
        assertNotEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 상세 페이지 공개 전환")
    public void startLearning() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        mockMvc.perform(get("/profile/learning/start/" + learning.getId()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/learning/" + learning.getId()));

        assertTrue(learning.isStartingLearning());
        assertFalse(learning.isClosedLearning());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 상세 페이지 공개 취소(닫기)")
    public void closedLearning() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = postLearning(account);

        mockMvc.perform(get("/profile/learning/close/" + learning.getId()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/learning/" + learning.getId()));

        assertFalse(learning.isStartingLearning());
        assertTrue(learning.isClosedLearning());
    }*/
}