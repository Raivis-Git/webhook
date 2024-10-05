package com.facebook.webhook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/facebook")
public class WebhookController {

    @GetMapping("/webhook")
    public String hello() {
        return "Hello, this is a secure HTTPS server!";
    }

}
