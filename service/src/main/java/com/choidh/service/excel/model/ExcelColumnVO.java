package com.choidh.service.excel.model;

import com.choidh.service.annotation.Name;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
@Builder
@Entity(name = "EXCEL_COLUMN")
//@Table(name = "EXCEL_COLUMN")
public class ExcelColumnVO {
    @EmbeddedId
    private ExcelColumnKey excelColumnKey;

    @Name(name = "컬럼 이름 (한글)")
    private String columnNameMk;

    @Name(name = "정렬 순서")
    @Column(unique = true)
    private String columnOrder;

//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createAt;
//    @Temporal(TemporalType.DATE)
//    private Date createDateAt;
//    @Temporal(TemporalType.TIME)
//    private Date createTimeAt;
}
