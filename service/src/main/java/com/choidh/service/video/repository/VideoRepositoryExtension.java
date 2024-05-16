package com.choidh.service.video.repository;


import com.choidh.service.learning.entity.Learning;
import com.choidh.service.video.entity.Video;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface VideoRepositoryExtension {

    List<Video> findByTitleAndLearning(String title, Learning learning);

}
