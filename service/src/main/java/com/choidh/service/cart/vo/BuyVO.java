package com.choidh.service.cart.vo;

import com.choidh.service.learning.entity.Learning;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class BuyVO {
    private List<Learning> learningList;

    private Map<Long, List<String>> learningImageMap;

    private int totalPrice;
}
