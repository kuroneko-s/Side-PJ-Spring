package com.choidh.service.learning.repository;


import com.choidh.service.learning.entity.QLearning;
import com.choidh.service.tag.entity.Tag;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LearningPredicates {

    public static Predicate findTop4ByTags(Set<Tag> tags){
        QLearning learning = QLearning.learning;
        List<String> tagList = tags.stream().map(Tag::getTitle).collect(Collectors.toList());

        if (tagList.isEmpty()) {
            return null;
        }

        return learning.tags.any().in(tags)
                .or(learning.title.containsIgnoreCase(tagList.get(0)))
                .or(learning.title.containsIgnoreCase(tagList.get(tagList.size()-1)));
    }

}
