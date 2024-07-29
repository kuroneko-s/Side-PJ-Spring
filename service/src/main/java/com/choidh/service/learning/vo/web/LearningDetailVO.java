package com.choidh.service.learning.vo.web;

import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.common.annotation.Name;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.question.entity.Question;
import com.choidh.service.review.entity.Review;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningDetailVO {
    @Name(name = "강의 정보")
    private Learning learning;

    @Name(name = "강의 관련 질문글")
    private Set<Question> question;

    @Name(name = "강의 관련 리뷰글")
    private Set<Review> reviews;

    @Name(name = "강의 제작자 정보")
    private ProfessionalAccount professionalAccount;

    @Name(name = "강의 배너 이미지")
    private AttachmentFile bannerImage;

    @Name(name = "강의 영상")
    private List<AttachmentFile> videoFilesList;

    @Name(name = "유저가 해당 강의를 듣고 있는지 여부")
    private boolean nowListening;
}
