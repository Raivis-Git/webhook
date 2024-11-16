package com.facebook.webhook.controller;

import com.facebook.webhook.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/facebook/webhook")
public class WebhookController {

    Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Value("${facebook.verify.token}")
    private String verifyToken;

    @Value("${facebook.access.token}")
    private String accessToken;
    @Autowired
    FacebookService facebookService;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Test successful");
    }

    @GetMapping
    public ResponseEntity<?> webhookSubscribe(@RequestBody String requestBody,
                                              @RequestParam("hub.verify_token") String receivedVerifyToken,
                                              @RequestParam("hub.challenge") String challenge,
                                              @RequestParam("hub.mode") String mode) {

        logger.info("webhook GET received from");

        if ("subscribe".equals(mode) && verifyToken.equals(receivedVerifyToken)) {
            logger.info("webhook VERIFIED");
            return ResponseEntity.ok(challenge);
        } else
            return ResponseEntity.status(403).build();

    }

    @PostMapping // Process messages from Facebook
    public ResponseEntity<?> processMessage(@RequestBody String requestBody) {
        logger.info("POST /webhook received \n" + requestBody);

        facebookService.processIncomingMessage(requestBody);

        // Always return a 200 status code to prevent retries
        return ResponseEntity.ok().build();
    }
}
