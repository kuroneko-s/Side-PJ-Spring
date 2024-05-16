package com.choidh.web.main.service;


import com.choidh.service.learning.entity.Learning;

import java.util.List;

public interface MainService {
    List<Learning> learningOrderByCreateLearning();

    List<Learning> learningOrderByRating();
}
