package com.choidh.service.mail.vo;


import com.choidh.service.account.entity.Account;
import com.choidh.service.common.annotation.Name;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageVO {
    @Name(name = "메일에 첨부될 이름의 역할")
    private String nickname;

    @Name(name = "메일에 첨부될 링크의 제목")
    private String linkName;

    @Name(name = "메일에 첨부될 내용")
    private String message;

    @Name(name = "메일에 첨부될 링크의 path 주소")
    private String link;

    @Name(name = "메일을 받을 주소")
    private String to;

    @Name(name = "메일의 제목")
    private String subject;

    @Name(name = "메일의 템플릿 경로", description = "유형에 따라 고정")
    private String templatePath;

    public static EmailMessageVO getInstanceForAccount(Account account) {
        return EmailMessageVO.builder()
                .nickname(account.getNickname())
                .linkName("이메일 인증하기")
                .message("이메일 인증을 마치시려면 링크를 클릭해주세요.")
                .link("/mailAuth?token=" + account.getTokenForEmailForAuthentication() + "&email=" + account.getEmail())
                .to(account.getEmail())
                .subject("회원 가입 안내 메일")
                .templatePath("mail/accountAuthentication")
                .build();
    }

    public static EmailMessageVO getInstanceForLearning(Account account, Learning learning, NotificationMailType notificationMailType) {
        EmailMessageVOBuilder builder = EmailMessageVO.builder()
                .nickname(account.getNickname())
                .linkName(learning.getTitle())
                .link("/learning/" + learning.getId())
                .to(account.getEmail())
                .templatePath("mail/learningNotification");

        switch (notificationMailType) {
            case CREATE:
                builder.subject("강의 생성 알림");
                builder.message("강의가 새롭게 생성되었습니다.");
                break;
            case UPDATE:
                builder.subject("강의 변경 알림");
                builder.message("강의 내용이 갱신되었습니다.");
                break;
            case CLOSE:
                builder.subject("강의 종료 알림");
                builder.message("강의가 종료되었습니다.");
                break;
        }

        return builder.build();
    }
}
