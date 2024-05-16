package com.choidh.service.excel.handler;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.Map;

public class DefaultResultHandler implements ResultHandler<Map<String, Object>> {

    @Override
    public void handleResult(ResultContext<? extends Map<String, Object>> context) {
        Object value = context.getResultObject();

        System.out.println("???????????##################?????????????????");
        System.out.println(value);
        // 여기서 Excel Column 정보 가져와서 바인딩하는 무언가를 부여해줘야지 가져올 수 있음.
//        K key = mo.getValue(this.mapKey);
//        this.mappedResults.put(key, value);
    }
}
