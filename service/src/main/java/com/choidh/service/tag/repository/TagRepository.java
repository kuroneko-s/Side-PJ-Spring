package com.choidh.service.tag.repository;


import com.choidh.service.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByTitle(String title);
}
