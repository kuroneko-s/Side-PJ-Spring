package com.choidh.service.excel.vo;

import com.choidh.service.common.annotation.Name;
import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ExcelColumnKey implements Serializable {
    @Name(name = "PK", description = "SQLMapper에서 사용하는 select 구분의 id와 동일해야함.")
    private String selectId;

    @Name(name = "컬럼 이름, PK")
    private String columnName;
}

