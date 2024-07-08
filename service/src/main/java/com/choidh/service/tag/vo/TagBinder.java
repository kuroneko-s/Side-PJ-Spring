package com.choidh.service.tag.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TagBinder {
    @JsonProperty("value")
    private String value;
}
