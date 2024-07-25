package com.choidh.service.joinTables.entity;

import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearningTagJoinTable extends BaseDateEntity {
    @Id
    @GeneratedValue
    @Column(name = "learnign_tag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "learning_id")
    @JsonBackReference
    private Learning learning;

    @Setter
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public void setLearning(Learning learning) {
        this.learning = learning;

        learning.getTags().add(this);
    }

}
