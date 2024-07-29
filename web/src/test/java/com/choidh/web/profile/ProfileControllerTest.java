package com.choidh.web.profile;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.service.AccountServiceImpl;
import com.choidh.service.account.vo.web.RegAccountVO;
import com.choidh.service.joinTables.service.AccountTagService;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.repository.TagRepository;
import com.choidh.service.tag.vo.RegTagVO;
import com.choidh.web.account.vo.AccountVO;
import com.choidh.web.config.WithAccount;
import com.choidh.web.profile.controller.ProfileController;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class ProfileControllerTest {
    private final MockMvc mockMvc;
    private final AccountRepository accountRepository;
    private final AccountServiceImpl accountServiceImpl;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AccountTagService accountTagService;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("프로필 대시보드 보여주기")
    @WithAccount("test@naver.com")
    public void getProfileViewDashBoard() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);

        mockMvc.perform(get("/profile/" + account.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learningTitle"))
                .andExpect(model().attributeExists("accountQuestion"))
                .andExpect(model().attributeExists("tagList"))
                .andExpect(model().attributeExists("learnings"))
                .andExpect(view().name("profile/profile"))
                .andExpect(status().isOk());

    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정 화면 보여주기 - 성공")
    public void getProfile_View_success() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        RegTagVO tag_1 = RegTagVO.builder()
                .title("java")
                .build();
        RegTagVO tag_2 = RegTagVO.builder()
                .title("spring")
                .build();
        accountTagService.regTag(tag_1, account.getId());
        accountTagService.regTag(tag_2, account.getId());

        mockMvc.perform(get("/profile/" + account.getId() + "/custom"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정 화면 보여주기 - 성공_빈값")
    public void getProfile_View_success_isEmpty() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);

        mockMvc.perform(get("/profile/" + account.getId() + "/custom"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정하기 - 성공")
    public void updateProfile_success() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);

        mockMvc.perform(post("/update/nickname/" + account.getId())
                .param("nickname", "테스트냥이_2")
                .param("description", "테스트 코드에용")
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeExists("account"))
                .andExpect(redirectedUrl("/profile/" + account.getId() + "/custom"))
                .andExpect(status().is3xxRedirection());

        Account newAccount = accountRepository.findByEmailAndChecked("test@naver.com", true);

        assertEquals(newAccount.getNickname(), "테스트냥이_2");
        assertEquals(newAccount.getDescription(), "테스트 코드에용");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정하기 - 실패(nickname pattern error)")
    public void updateProfile_fail_pattern() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);

        mockMvc.perform(post("/update/nickname/" + account.getId())
                .param("nickname", "테스트냥이_2( )")
                .param("description", "테스트 코드에용")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByEmailAndChecked("test@naver.com", true);

        assertNotEquals(newAccount.getNickname(), "테스트냥이_2( )");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정하기 - 실패(nickname duplication)")
    public void updateProfile_fail_duplication() throws Exception{
        Account account_1 = accountRepository.findByEmailAndChecked("test@naver.com", true);

        AccountVO accountVO = new AccountVO();
        accountVO.setNickname("테스트냥이2");
        accountVO.setEmail("test2@naver.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        Account account_2 = accountServiceImpl.regAccount(modelMapper.map(accountVO, RegAccountVO.class));
        account_2.setChecked(true);

        mockMvc.perform(post("/update/nickname/" + account_1.getId())
                .param("nickname", "테스트냥이")
                .param("description", "테스트 코드에용")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByEmailAndChecked("test@naver.com", true);

        assertNotEquals(newAccount.getNickname(), "테스트냥이2");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정하기 - 실패(description empty and null)")
    public void updateProfile_fail_description() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        account.setDescription("테스트용 문구");

        mockMvc.perform(post("/update/nickname/" + account.getId())
                .param("nickname", "테스트냥이_3")
                .param("description", "")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByEmailAndChecked("test@naver.com", true);

        assertNotEquals(newAccount.getNickname(), "테스트냥이_3");
        log.info("=====================info=====================");
        log.info(newAccount.getDescription());
        log.info(newAccount.getNickname());
        log.info("=====================info=====================");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 성공")
    public void updateProfilePassword_success() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String newPassword = "0987654321";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", "1234567890")
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPassword)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/" + account.getId() + "/custom"));

        Account newAccount = accountRepository.findByEmailAndChecked("test@naver.com", true);

        assertTrue(passwordEncoder.matches(newPassword, newAccount.getPassword()));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 실패_passwordForm unmatch Pattern_min")
    public void updateProfilePassword_fail_pattern_min() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String newPassword = "0987654";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", "1234567890")
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPassword)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE));

        Account newAccount = accountRepository.findByEmailAndChecked("test@naver.com", true);

        assertFalse(passwordEncoder.matches(newPassword, newAccount.getPassword()));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 실패_passwordForm unmatch Pattern_max")
    public void updateProfilePassword_fail_pattern_max() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String newPassword = "0987654wequiosaklnlvzroppqnfslb!@$padj!212@mdpowqn!!@";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", "1234567890")
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPassword)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE));

        Account newAccount = accountRepository.findByEmailAndChecked("test@naver.com", true);

        assertFalse(passwordEncoder.matches(newPassword, newAccount.getPassword()));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 실패_passwordForm unmatch validator nowPassword")
    public void updateProfilePassword_fail_validator_unmatch_now() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String nowPassword = "12345678qrwq#@!";
        String newPassword = "0987654wequinlvzroppqnfslb!@$padj!212@mdpow";
        String newPasswordCheck = "0987654wequinlvzroppqnfslb!@$padj!212@mdpow";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", nowPassword)
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPasswordCheck)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE));

        Account newAccount = accountRepository.findByEmailAndChecked("test@naver.com", true);

        assertFalse(passwordEncoder.matches(nowPassword, account.getPassword()));
        assertFalse(passwordEncoder.matches(newPasswordCheck, newAccount.getPassword()));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 실패_passwordForm unmatch validator newPassword")
    public void updateProfilePassword_fail_validator_unmatch_new() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        String newPassword = "0987654wequinlvzroppqnfslb!@$padj!212@mdpow";
        String newPasswordCheck = "0987654wequinlvzroppqnfslb!@$padj!212@mdpowqn@";
        String nowPassword = "1234567890";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", nowPassword)
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPasswordCheck)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE));

        assertTrue(passwordEncoder.matches(nowPassword, account.getPassword()));
        assertNotEquals(newPassword, newPasswordCheck);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 알림 설정 - 성공")
    public void updateProfileNotifications_success() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);

        mockMvc.perform(post("/update/noti/" + account.getId())
                .param("siteMailNotification", "true")
                .param("siteWebNotification", "true")
                .param("learningMailNotification", "true")
                .param("learningWebNotification", "true")
                .with(csrf()))
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/profile/" + account.getId() + "/custom"))
                .andExpect(status().is3xxRedirection());

        assertTrue(account.isSiteMailNotification());
        assertTrue(account.isSiteWebNotification());
        assertTrue(account.isLearningMailNotification());
        assertTrue(account.isLearningWebNotification());
    } //실패 가능성 0

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 태그 추가 - 성공")
    public void updateProfileTags_success() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);

        mockMvc.perform(post("/update/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("test_Tag"))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("test_Tag");

        assertTrue( account.getTags().contains(newTag));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 태그 추가 중복값 - 성공")
    public void updateProfileTagsDuplication_success() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        accountTagService.regTag(RegTagVO.builder().title("test_Tag").build(), account.getId());

        mockMvc.perform(post("/update/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("test_Tag"))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("test_Tag");

        assertTrue(account.getTags().contains(newTag));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 태그 삭제 - 성공")
    public void removeProfileTags_success() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);
        accountTagService.regTag(RegTagVO.builder().title("test_Tag").build(), account.getId());

        mockMvc.perform(post("/update/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("test_Tag"))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("test_Tag");

        assertNotNull(newTag);
        assertFalse(account.getTags().contains(newTag));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 태그 삭제 - 실패")
    public void removeProfileTags_fail() throws Exception{
        Account account = accountRepository.findByEmailAndChecked("test@naver.com", true);

        mockMvc.perform(post("/update/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("test_Tag"))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        Tag newTag = tagRepository.findByTitle("test_Tag");

        assertNull(newTag);
    }

}