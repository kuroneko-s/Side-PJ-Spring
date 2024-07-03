package com.choidh.service.joinTables.entity;

import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.notice.entity.Notice;
import lombok.*;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearningNoticeJoinTable extends BaseDateEntity {
    @Id
    @GeneratedValue
    @Column(name = "learnign_notice_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;

    @Setter
    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public void setLearning(Learning learning) {
        this.learning = learning;
        learning.getNotices().add(this);
    }

}
