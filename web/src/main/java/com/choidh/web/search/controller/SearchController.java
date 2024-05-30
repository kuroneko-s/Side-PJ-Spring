package com.choidh.web.search.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final LearningRepository learningRepository;

    // 모든 강의 조회
    @GetMapping("/all")
    public String viewAll(@CurrentAccount Account account, Model model,
                          @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable) {
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findAllWithPageable(true, pageable);

        model.addAttribute("pathLink", "모든 강의");
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/all");
        model.addAttribute("keyword", null);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }

    // 웹 관련 전체 검색
    @GetMapping("/web/all")
    public String viewWebAll(@CurrentAccount Account account, Model model,
                             @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findByKategorieWithPageable(true, "web" , pageable);

        model.addAttribute("pathLink", List.of("Web", "All"));
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/web/all");
        model.addAttribute("keyword", null);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }

    // 웹 관련 Java 검색
    @GetMapping("/web/java")
    public String viewWebJava(@CurrentAccount Account account, Model model, String keyword,
                              @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findByKategorieAndKeywordWithPageable(true, keyword, "web" , pageable);

        model.addAttribute("pathLink", List.of("Web", "Java"));
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/web/java");
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }

    // 웹 관련 javascript 검색
    @GetMapping("/web/javascript")
    public String viewWebJavascript(@CurrentAccount Account account, Model model, String keyword,
                                    @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findByKategorieAndKeywordWithPageable(true, keyword, "web" , pageable);

        model.addAttribute("pathLink", List.of("Web", "Javascript"));
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/web/javascript");
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }

    //웹 관련 CSS_HTML 검색
    @GetMapping("/web/html")
    public String viewWebCSS_HTML(@CurrentAccount Account account, Model model, String keyword,
                                  @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findByKategorieAndKeywordWithPageable(true, keyword, "web" , pageable);

        model.addAttribute("pathLink", List.of("Web", "HTML&CSS"));
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/web/html");
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }

    // 알고리즘 관련 ALL 검색
    @GetMapping("/algorithm/all")
    public String viewAlgorithmAll(@CurrentAccount Account account, Model model,
                                   @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findByKategorieWithPageable(true, "algorithm" , pageable);

        model.addAttribute("pathLink", List.of("Algorithm", "All"));
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/algorithm/all");
        model.addAttribute("keyword", null);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }

    // 알고리즘 GOF
    @GetMapping("/algorithm/gof")
    public String viewAlgorithmGOF(@CurrentAccount Account account, Model model, String keyword,
                                   @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findByKategorieAndKeywordWithPageable(true, keyword,"algorithm" , pageable);

        model.addAttribute("pathLink", List.of("Algorithm", "GOF"));
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/algorithm/gof");
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }

    // 알고리즘 알고리즘
    @GetMapping("/algorithm/algorithm")
    public String viewAlgorithm(@CurrentAccount Account account, Model model, String keyword,
                                @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findByKategorieAndKeywordWithPageable(true, keyword,"algorithm" , pageable);

        model.addAttribute("pathLink", List.of("Algorithm", "Algorithm"));
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/algorithm/algorithm");
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }

    @GetMapping("/search/learning")
    public String searchLearning(@CurrentAccount Account account, String keyword, Model model,
                                 @PageableDefault(size = 16, sort = "openLearning", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) model.addAttribute(account);

        Page<Learning> pageableLearning = learningRepository.findByKeywordWithPageable(keyword, pageable);

        model.addAttribute("pathLink", "검색 결과");
        model.addAttribute("learningList", pageableLearning);
        model.addAttribute("url", "/all");
        model.addAttribute("keyword", null);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("openLearning") ? "openLearning" : "rating");

        return "learning/program";
    }
}
