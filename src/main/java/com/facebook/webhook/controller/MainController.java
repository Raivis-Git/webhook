package com.facebook.webhook.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class MainController {

    @GetMapping
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Welcome to nuke 55");
    }

}
