package com.choidh.app.learnieng;

import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.learning.vo.api.LearningResource;
import com.choidh.service.learning.vo.api.LearningVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.choidh.app.common.AppConstant.API_DEFAULT_PATH;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
                                          @RequestParam(required = false) String subCategory,
                                          @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                          PagedResourcesAssembler<LearningVO> assembler) {
        PagedModel<EntityModel<LearningVO>> pagedModel = assembler.toModel(learningService.getLearningPagingWithKeywordForAPI(mainCategory, subCategory, pageable));

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Get 강의 단건 조회
     */
    @GetMapping("/{learningId}")
    public ResponseEntity getLearningDetail(@PathVariable Long learningId) {
        Learning learning = learningService.getLearningById(learningId);
        LearningVO learningVO = new LearningVO(learning);

        LearningResource learningResource = new LearningResource(learningVO);
        learningResource.add(linkTo(LearningController.class).slash(learningId).withSelfRel());

        return ResponseEntity.ok(learningResource);
    }
}
