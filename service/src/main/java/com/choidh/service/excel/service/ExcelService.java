package com.choidh.service.excel.service;

import com.choidh.service.excel.vo.ExcelVO;
import com.sun.istack.NotNull;

import javax.servlet.http.HttpServletResponse;

public interface ExcelService {
    void defaultDownload(HttpServletResponse response, @NotNull ExcelVO excelVO);
}
