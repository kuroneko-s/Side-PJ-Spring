package com.choidh.service.video.repository;


import com.choidh.service.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryExtension, QuerydslPredicateExecutor<Video> {
}
