package com.choidh.service.account.repository;

import com.choidh.service.account.entity.ApiAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiAccountRepository extends JpaRepository<ApiAccount, Long> {
    ApiAccount findByEmail(String email);
}
