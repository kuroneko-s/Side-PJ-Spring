package com.choidh.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/")
    public ResponseEntity home() {
        return ResponseEntity.ok("wow");
    }

    @GetMapping("/restapi/sample")
    public String sample() {
        return "hello";
    }
}
