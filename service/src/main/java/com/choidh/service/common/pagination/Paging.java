package com.choidh.service.common.pagination;

import com.choidh.service.common.annotation.Name;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paging {
    @Name(name = "페이지네이션 기본 버튼 Url")
    private String paginationUrl;

    @Name(name = "이전 버튼 활성화 유무")
    private boolean hasPrevious;

    @Name(name = "다음 버튼 활성화 유무")
    private boolean hasNext;

    @Name(name = "현재 페이지")
    private int number;

    @Name(name = "총 페이지")
    private int totalPages;
}
