package com.choidh.service.excel.init;

import com.choidh.service.excel.init.dummy.ExcelDummyDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Service
public class ExcelInitializers {
    private final ExcelDummyDataService excelDummyDataService;

    public ExcelInitializers(ExcelDummyDataService excelDummyDataService) {
        this.excelDummyDataService = excelDummyDataService;
    }

    @PostConstruct
    @Transactional
    public void initExcelColumn() {
        this.excelDummyDataService.savedSampleDate();
    }
}
