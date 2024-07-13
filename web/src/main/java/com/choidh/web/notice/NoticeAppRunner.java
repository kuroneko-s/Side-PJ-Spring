package com.choidh.web.notice;

import com.choidh.service.notice.service.NoticeService;
import com.choidh.service.notice.vo.NoticeListResult;
import com.choidh.service.notice.vo.NoticeType;
import com.choidh.service.notice.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class NoticeAppRunner implements ApplicationRunner {
    @Autowired
    private NoticeService noticeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        NoticeListResult beforeList = noticeService.getNoticeListResultForAdmin(PageRequest.of(0, 10));

        if (beforeList.getNoticeList().isEmpty()) {
            // 관리자 화면에서 보여줄 공지사항 생성
            for (int i = 0; i < 5; i++) {
                noticeService.regNotice(NoticeVO.builder()
                        .title("샘플 공지사항 " + i)
                        .content("샘플 공지사항 내용 " + i)
                        .noticeType(NoticeType.WEB)
                        .build());
            }
        }
    }
}
