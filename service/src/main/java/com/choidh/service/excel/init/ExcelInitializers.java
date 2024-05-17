package com.choidh.service.excel.init;

import com.choidh.service.excel.init.dummy.ExcelDummyDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ExcelInitializers {
    private final ExcelDummyDataService excelDummyDataService;

    @Transactional
    public void initExcelColumn() {
        this.excelDummyDataService.savedSampleDate();
    }
}
