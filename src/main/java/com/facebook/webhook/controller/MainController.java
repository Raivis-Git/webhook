package com.facebook.webhook.controller;

import com.facebook.webhook.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facebook/send/")
public class MainController {

    @Autowired
    public FacebookMessageService facebookMessageService;

    @GetMapping("/{recipientId}")
    public ResponseEntity<?> sendMessageFromTemplate(@PathVariable String recipientId) {
        facebookMessageService.sendWelcomeMessage(recipientId);
        return ResponseEntity.ok("Welcome to nuke 55");
    }

}
