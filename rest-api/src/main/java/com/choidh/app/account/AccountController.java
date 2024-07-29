package com.choidh.app.account;

import com.choidh.service.account.entity.ApiAccount;
import com.choidh.service.account.repository.ApiAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.choidh.app.common.AppConstant.API_DEFAULT_PATH;

@RestController
@RequestMapping(value = API_DEFAULT_PATH + "/account", produces = "application/hal+json")
public class AccountController {
    @Autowired
    private ApiAccountRepository apiAccountRepository;

    @PostMapping("/{accountId}")
    public ResponseEntity getAccount(@PathVariable Long accountId) {
        Optional<ApiAccount> apiAccountOptional = apiAccountRepository.findById(accountId);

        if (apiAccountOptional.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(apiAccountOptional.get());
    }
}
