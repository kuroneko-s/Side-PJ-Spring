package com.choidh.service.excel.repository;

import com.choidh.service.excel.model.ExcelColumnKey;
import com.choidh.service.excel.model.ExcelColumnVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExcelColumnRepository extends JpaRepository<ExcelColumnVO, ExcelColumnKey> {
    @Query("SELECT ec FROM EXCEL_COLUMN ec where ec.excelColumnKey.selectId = :id ORDER BY ec.columnOrder")
    List<ExcelColumnVO> findAllById(@Param("id") String id);
}
