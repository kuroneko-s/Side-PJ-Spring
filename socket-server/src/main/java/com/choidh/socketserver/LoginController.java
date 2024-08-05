package com.choidh.socketserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    @GetMapping("login")
    public ResponseEntity login() {
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/login/success")
    public ResponseEntity loginSuccess(@RequestParam("token") String token) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("token", token);

        return ResponseEntity.ok(resultMap);
    }
}
