package com.choidh.service.excel.init.dummy;

import com.choidh.service.excel.model.ExcelColumnKey;
import com.choidh.service.excel.model.ExcelColumnVO;
import com.choidh.service.excel.repository.ExcelColumnRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("local")
@Service
public class ExcelDummyDataLocalServiceImpl implements ExcelDummyDataService {
    private final ExcelColumnRepository excelColumnRepository;

    public ExcelDummyDataLocalServiceImpl(ExcelColumnRepository excelColumnRepository) {
        this.excelColumnRepository = excelColumnRepository;
    }

    public void savedSampleDate() {
        List<ExcelColumnVO> getAll = this.excelColumnRepository.findAllById("getAll");
        if (getAll.isEmpty()) {
            ExcelColumnVO one = ExcelColumnVO.builder()
                    .excelColumnKey(
                            ExcelColumnKey.builder()
                                    .selectId("getAll")
                                    .columnName("username")
                                    .build()
                    )
                    .columnNameMk("이름")
                    .columnOrder("1")
                    .build();
            this.excelColumnRepository.save(one);

            ExcelColumnVO two = ExcelColumnVO.builder()
                    .excelColumnKey(
                            ExcelColumnKey.builder()
                                    .selectId("getAll")
                                    .columnName("email")
                                    .build()
                    )
                    .columnNameMk("이메일")
                    .columnOrder("2")
                    .build();
            this.excelColumnRepository.save(two);

            this.excelColumnRepository.flush();
        }
    }
}
