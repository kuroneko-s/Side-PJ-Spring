package com.choidh.app.login;

import com.choidh.app.account.AccountController;
import com.choidh.app.learnieng.LearningController;
import com.choidh.app.login.vo.LoginResource;
import com.choidh.app.login.vo.LoginResponse;
import com.choidh.service.account.entity.ApiAccount;
import com.choidh.service.account.repository.ApiAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
public class LoginController {
    @Autowired
    private ApiAccountRepository accountRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login/success")
    @ResponseBody
    public ResponseEntity loginSuccess(@RequestParam("token") String token,
                                       @RequestParam("expiration") Long expiration,
                                       @RequestParam("email") String email) {
        ApiAccount apiAccount = accountRepository.findByEmail(email);
        LoginResource loginResource = new LoginResource(
                LoginResponse.builder()
                        .token(token)
                        .expiration(expiration)
                        .build(),
                linkTo(LearningController.class).slash("list").withRel("강의 목록 조회"),
                linkTo(AccountController.class).slash(apiAccount.getId()).withRel("계정 정보 조회")
        );

        return ResponseEntity.ok(loginResource);
    }
}
