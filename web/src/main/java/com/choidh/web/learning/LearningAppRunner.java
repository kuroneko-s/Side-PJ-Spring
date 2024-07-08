package com.choidh.web.learning;

import com.choidh.service.account.entity.Account;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.professional.repository.ProfessionalAccountAccountRepository;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.vo.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.repository.AttachmentFileRepository;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.common.utiles.FileUtils;
import com.choidh.service.common.utiles.StringUtils;
import com.choidh.service.common.exception.FileNotSavedException;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.question.service.QuestionService;
import com.choidh.service.question.vo.RegQuestionVO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Component
public class LearningAppRunner implements ApplicationRunner {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfessionalAccountAccountRepository professionalAccountRepository;
    @Autowired
    private LearningRepository learningRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private AttachmentFileRepository attachmentFileRepository;
    @Autowired
    private QuestionService questionService;

    @Value("${download.path}")
    private String downloadPath;

    private void persistClear() {
        entityManager.flush();
        entityManager.clear();
    }

    protected Account createAccount(String nickname, String email) {
        Account account = Account.builder()
                .nickname(nickname)
                .email(email)
                .password(passwordEncoder.encode("1234567890"))
                .checked(true)
                .tags(new HashSet<>())
                .questions(new HashSet<>())
                .reviews(new HashSet<>())
                .purchaseHistories(new HashSet<>())
                .build();
        account.createTokenForEmailForAuthentication();

        return accountRepository.save(account);
    }

    private ProfessionalAccount createProfessionalAccount(Account account) {
        return professionalAccountRepository.save(ProfessionalAccount.builder()
                .account(account)
                .name("강사 이름")
                .description("개잘나가는 강사임")
                .history("개쩌는 경력들")
                .used(true)
                .build());
    }

    private Learning createLearning(ProfessionalAccount professionalAccount) {
        int floor = (int) Math.floor(Math.random() * 2);

        Learning learning = Learning.builder()
                .title("샘플 강의 1")
                .simpleSubscription("간단한 강의 설명")
                .subscription("장황한 강의 설명글")
                .mainCategory(floor % 2 == 0? "자바" : "자바스크립트")
                .subCategory("알고리즘")
                .price(10000)
                .rating((int) Math.floor(Math.random() * 5) + 1)
                .opening(true)
                .openingDate(LocalDateTime.now())
                .attachmentGroup(null)
                .professionalAccount(professionalAccount)
                .tags(new HashSet<>())
                .questions(new HashSet<>())
                .reviews(new HashSet<>())
                .purchaseHistories(new HashSet<>())
                .build();

        return learningRepository.save(learning);
    }

    private Learning createLearning(ProfessionalAccount professionalAccount, String title) {
        int floor = (int) Math.floor(Math.random() * 2);

        Learning learning = Learning.builder()
                .title(title)
                .simpleSubscription("간단한 강의 설명")
                .subscription("장황한 강의 설명글")
                .mainCategory(floor % 2 == 0? "자바" : "자바스크립트")
                .subCategory("BackEnd")
                .price(10000)
                .rating((int) Math.floor(Math.random() * 5) + 1)
                .opening(true)
                .openingDate(LocalDateTime.now())
                .attachmentGroup(null)
                .professionalAccount(professionalAccount)
                .tags(new HashSet<>())
                .questions(new HashSet<>())
                .reviews(new HashSet<>())
                .purchaseHistories(new HashSet<>())
                .build();

        return learningRepository.save(learning);
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        List<Learning> learningList = learningRepository.findAll();

        if (learningList.isEmpty()) {
            Account account = createAccount("sample Runner Account", "sampleRunnerAccount@test.com");
            persistClear();
            ProfessionalAccount professionalAccount = createProfessionalAccount(account);
            persistClear();
            for (int i = 0; i < 48; i++) {
                // 첨부파일 정보도 같이 넘겨줘야함.
                AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();

                Learning learning = createLearning(professionalAccount);
                learning.setAttachmentGroup(attachmentGroup);

                RegQuestionVO regQuestionVO = RegQuestionVO.builder()
                        .title("이거 어디까지 올라가는거에요 ?")
                        .description("이거 어디까지 올라가는거에요 ?")
                        .build();
                questionService.regQuestion(regQuestionVO, account.getId(), learning.getId());

                regQuestionVO = RegQuestionVO.builder()
                        .title("이거 어디까지 올라가는거에요 ?2")
                        .description("이거 어디까지 올라가는거에요 ?2")
                        .build();
                questionService.regQuestion(regQuestionVO, account.getId(), learning.getId());

                createFile(attachmentGroup);
                createVideoFile1(attachmentGroup);
                createVideoFile2(attachmentGroup);
            }
        }
    }

    private void createFile(AttachmentGroup attachmentGroup) throws IOException {
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));
        File file = resource.getFile();

        AttachmentFile beforeAttachmentFile = new AttachmentFile(attachmentGroup);
        AttachmentFile newAttachmentFile = attachmentFileRepository.save(beforeAttachmentFile);
        String groupSn = StringUtils.padLeftUsingFormat(attachmentGroup.getId().toString(), 7, '0');
        String fileSn = StringUtils.padLeftUsingFormat(newAttachmentFile.getId().toString(), 7, '0');

        String filePath = FileUtils.getFilePath();
        String fileName = groupSn + fileSn;
        String fileExtension = "jpg";

        newAttachmentFile.setFileSize(file.length());
        newAttachmentFile.setOriginalFileName(resource.getFilename());
        newAttachmentFile.setFileName(fileName);
        newAttachmentFile.setPath(filePath);
        newAttachmentFile.setExtension(fileExtension);
        newAttachmentFile.setAttachmentFileType(AttachmentFileType.BANNER);

        // 위치 폴더 생성
        FileUtils.createDir(downloadPath + filePath);
        try(
                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newAttachmentFile.getFullPath(downloadPath)), 1024 * 500)
        ){
            // 파일 저장
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            // 로컬에 파일 저장에 실패했을 경우 기존 DB 정보도 삭제.
            attachmentFileRepository.delete(newAttachmentFile);
            throw new FileNotSavedException(e);
        }
    }

    private void createVideoFile1(AttachmentGroup attachmentGroup) throws IOException {
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/video/sample_1.mp4"));
        File file = resource.getFile();

        AttachmentFile beforeAttachmentFile = new AttachmentFile(attachmentGroup);
        AttachmentFile newAttachmentFile = attachmentFileRepository.save(beforeAttachmentFile);
        String groupSn = StringUtils.padLeftUsingFormat(attachmentGroup.getId().toString(), 7, '0');
        String fileSn = StringUtils.padLeftUsingFormat(newAttachmentFile.getId().toString(), 7, '0');

        String filePath = FileUtils.getFilePath();
        String fileName = groupSn + fileSn;
        String fileExtension = "mov";

        newAttachmentFile.setFileSize(file.length());
        newAttachmentFile.setOriginalFileName(resource.getFilename());
        newAttachmentFile.setFileName(fileName);
        newAttachmentFile.setPath(filePath);
        newAttachmentFile.setExtension(fileExtension);
        newAttachmentFile.setAttachmentFileType(AttachmentFileType.VIDEO);

        // 위치 폴더 생성
        FileUtils.createDir(downloadPath + filePath);
        try(
                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newAttachmentFile.getFullPath(downloadPath)), 1024 * 500)
        ){
            // 파일 저장
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            // 로컬에 파일 저장에 실패했을 경우 기존 DB 정보도 삭제.
            attachmentFileRepository.delete(newAttachmentFile);
            throw new FileNotSavedException(e);
        }
    }

    private void createVideoFile2(AttachmentGroup attachmentGroup) throws IOException {
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/video/sample_1.mp4"));
        File file = resource.getFile();

        AttachmentFile beforeAttachmentFile = new AttachmentFile(attachmentGroup);
        AttachmentFile newAttachmentFile = attachmentFileRepository.save(beforeAttachmentFile);
        String groupSn = StringUtils.padLeftUsingFormat(attachmentGroup.getId().toString(), 7, '0');
        String fileSn = StringUtils.padLeftUsingFormat(newAttachmentFile.getId().toString(), 7, '0');

        String filePath = FileUtils.getFilePath();
        String fileName = groupSn + fileSn;
        String fileExtension = "mov";

        newAttachmentFile.setFileSize(file.length());
        newAttachmentFile.setOriginalFileName(resource.getFilename());
        newAttachmentFile.setFileName(fileName);
        newAttachmentFile.setPath(filePath);
        newAttachmentFile.setExtension(fileExtension);
        newAttachmentFile.setAttachmentFileType(AttachmentFileType.VIDEO);

        // 위치 폴더 생성
        FileUtils.createDir(downloadPath + filePath);
        try(
                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newAttachmentFile.getFullPath(downloadPath)), 1024 * 500)
        ){
            // 파일 저장
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            // 로컬에 파일 저장에 실패했을 경우 기존 DB 정보도 삭제.
            attachmentFileRepository.delete(newAttachmentFile);
            throw new FileNotSavedException(e);
        }
    }
}
