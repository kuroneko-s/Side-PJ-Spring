package com.choidh.service.excel.service;

import com.choidh.service.common.exception.CustomException;
import com.choidh.service.common.timmer.Timer;
import com.choidh.service.excel.handler.DefaultResultHandler;
import com.choidh.service.excel.vo.ExcelColumnVO;
import com.choidh.service.excel.vo.ExcelVO;
import com.choidh.service.excel.repository.ExcelColumnRepository;
import com.sun.istack.NotNull;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Validated
public class ExcelServiceImpl implements ExcelService {
    private final ExcelColumnRepository excelColumnRepository;
    private final SqlSession session;
    private final EntityManagerFactory entityManagerFactory;

    public ExcelServiceImpl(ExcelColumnRepository excelColumnRepository, SqlSession session, EntityManagerFactory entityManagerFactory) {
        this.excelColumnRepository = excelColumnRepository;
        this.session = session;
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * TODO: 엑셀 기능 정의
     * DB에서 특정 조회 쿼리를 이용해서 컬럼에 바인딩 잡아주고 그 값을 기반으로 저장.
     * 추후에는 차트같은것도 작성가능한 기능 제공
     */
    @Timer
    @Override
    public void defaultDownload(HttpServletResponse response, @NotNull ExcelVO excelVO) {
        // 저장 컬럼들 조회
        List<ExcelColumnVO> columnVOList = this.excelColumnRepository.findAllById(excelVO.getSqlSelectName());

        // 저장 대상 값들 조회
        List<Map<String, Object>> resultList = this.session.selectList(excelVO.getSqlSelectName());

        this.session.select(excelVO.getSqlSelectName(), excelVO, new DefaultResultHandler());

        // Creating a Workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Creating a Sheet
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        // Creating a Row
        // 제목 Row 생성
        this.drawTitleRow(workbook, sheet, columnVOList);

        // 내용 Row 생성
        this.drawBodyRow(workbook, sheet, columnVOList, resultList);

        // 셀 크기 자동조정
        for (int i = 0; i < columnVOList.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Create a drawing patriarch
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 20);

        // Create a chart
        XSSFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        // Define data sources
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(0, 0, 0, columnVOList.size() - 1));
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(1, resultList.size(), 0, columnVOList.size()));

        // Create chart axis
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Category");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Value");

        // Set chart data
        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        ((XDDFBarChartData) data).setBarDirection(BarDirection.COL);

        // Add series to the chart
        XDDFChartData.Series series = data.addSeries(categories, values);
        series.setTitle("Data", null);
        chart.plot(data);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=sample.xlsx");

        try {
            ServletOutputStream outputStream = response.getOutputStream();

            workbook.write(outputStream);

            workbook.close();
        } catch (IOException e) {
            throw new CustomException("엑셀 저장 실패", e);
        }
    }

    // 내용 열 그리기
    private void drawBodyRow(Workbook workbook, Sheet sheet, List<ExcelColumnVO> columnVOList, List<Map<String, Object>> resultList) {
        CellStyle commonCellStyle = workbook.createCellStyle();
        commonCellStyle.setAlignment(HorizontalAlignment.CENTER);
        commonCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 내부 데이터 생성
        for (int rowIndex = 1; rowIndex < resultList.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex);

            Map<String, Object> cellTarget = resultList.get(rowIndex);
            for (int cellIndex = 0; cellIndex < columnVOList.size(); cellIndex++) {
                Cell cell = row.createCell(cellIndex);

                ExcelColumnVO excelColumnVO = columnVOList.get(cellIndex);
                String value = cellTarget.get(excelColumnVO.getExcelColumnKey().getColumnName()).toString();
                cell.setCellValue(value);
                cell.setCellStyle(commonCellStyle);
            }
        }
    }

    // 제목 열 그리기
    private void drawTitleRow(Workbook workbook, Sheet sheet, List<ExcelColumnVO> columnVOList) {
        Row row = sheet.createRow(0);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);

        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleFont);
        // 배경색상
        titleCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 정렬
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 테두리 설정
        titleCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        titleCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        titleCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        titleCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        titleCellStyle.setBorderTop(BorderStyle.THIN);
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);

        for (int cellIndex = 0; cellIndex < columnVOList.size(); cellIndex++) {
            Cell cell = row.createCell(cellIndex);

            ExcelColumnVO excelColumnVO = columnVOList.get(cellIndex);
            cell.setCellValue(excelColumnVO.getColumnNameMk());
            cell.setCellStyle(titleCellStyle);
        }
    }
}
