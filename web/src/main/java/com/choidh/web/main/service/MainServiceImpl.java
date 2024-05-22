package com.choidh.web.main.service;


import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MainServiceImpl implements MainService {
    private final LearningRepository learningRepository;

    @Override
    public List<Learning> learningOrderByCreateLearning() {
        return learningRepository.findTop4ByStartingLearningOrderByCreateLearningDesc(true);
    }

    @Override
    public List<Learning> learningOrderByRating() {
        return learningRepository.findTop12ByStartingLearningOrderByRatingDesc(true);
    }
}
