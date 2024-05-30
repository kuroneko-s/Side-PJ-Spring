package com.choidh.service.tag.entity;

import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(of = "id")
@Builder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Tag {

    @GeneratedValue
    @Id @Column(name = "tag_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    @OneToMany(mappedBy = "tag")
    private List<LearningTagJoinTable> learningTagJoinTables = new ArrayList<>();
}
