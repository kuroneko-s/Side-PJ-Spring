package com.choidh.app.learnieng;

import com.choidh.app.learnieng.vo.LearningResource;
import com.choidh.app.learnieng.vo.LearningVO;
import com.choidh.app.learnieng.vo.LearningVOList;
import com.choidh.service.learning.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static com.choidh.app.common.AppConstant.API_DEFAULT_PATH;

@RestController
@RequestMapping(value = API_DEFAULT_PATH + "/learning", produces = "application/hal+json")
@RequiredArgsConstructor
public class LearningController {
    private final LearningService learningService;

    /**
     * Get 강의 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity getLearningList(@RequestParam String mainCategory,
                                          @RequestParam String subCategory,
                                          @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
                                          /*PagedResourcesAssembler<LearningVO> assembler*/) {
        LearningVOList learningVOList = new LearningVOList(learningService.getTop12LearningListByOpeningDate().stream().map(LearningVO::new).collect(Collectors.toList()));
        LearningResource learningResource = new LearningResource(learningVOList);

        return ResponseEntity.ok(learningResource);
    }
}
