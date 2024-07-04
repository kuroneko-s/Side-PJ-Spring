package com.choidh.service.tag.service;

import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.joinTables.repository.LearningTagRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.repository.TagRepository;
import com.choidh.service.tag.vo.RegTagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.choidh.service.common.vo.AppConstant.getLearningNotFoundErrorMessage;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final LearningRepository learningRepository;
    private final LearningTagRepository learningTagRepository;

    // 태그 생성
    @Override
    public Tag regTag(RegTagVO regTagVO) {
        String title = regTagVO.getTitle();

        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            return tagRepository.save(Tag.builder()
                    .title(title)
                    .build());
        }

        return tag;
    }

    // 태그 타이틀 목록 조회
    @Override
    public List<String> getTitleList() {
        return tagRepository.findAll()
                .stream().map(Tag::getTitle)
                .collect(Collectors.toList());
    }

    // 태그 추가 For 강의
    @Override
    @Transactional
    public void addTagsForLearning(String tagTitle, Long learningId) {
        LearningTagJoinTable learningTagJoinTable = new LearningTagJoinTable();

        Learning learning = learningRepository.findById(learningId)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(learningId)));

        Tag tag = tagRepository.findByTitle(tagTitle);
        if (tag == null) {
            tag = Tag.builder().title(tagTitle).build();
            tagRepository.save(tag);
        }

        learningTagJoinTable.setLearning(learning);
        learningTagJoinTable.setTag(tag);

        learningTagRepository.save(learningTagJoinTable);
    }

    // 태그 삭제 For 강의
    @Override
    public int removeTagsForLearning(Long learningTagJoinTableId) {
        return learningTagRepository.deleteByLearningIdAndTagTitle(learningTagJoinTableId);
    }

    // 태그 단건 조회 By Title
    @Override
    public Tag getTagByTitle(String tagTitle) {
        return tagRepository.findByTitle(tagTitle);
    }
}
