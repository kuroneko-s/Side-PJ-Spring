package com.choidh.service.account.entity;

import com.choidh.service.account.vo.api.ApiAccountType;
import com.choidh.service.common.entity.BaseDateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity @Builder
@AllArgsConstructor @NoArgsConstructor
public class ApiAccount extends BaseDateEntity {
    @Id
    @GeneratedValue
    @Column(name = "API_ACCOUNT_ID")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ApiAccountType type;
}
