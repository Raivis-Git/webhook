package com.facebook.webhook.controller;

import com.facebook.webhook.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/facebook/webhook")
public class WebhookController {

    Logger LOGGER = LoggerFactory.getLogger(WebhookController.class);
    @Autowired
    ConfigLoader configLoader;

    @GetMapping("/test")
    public String test() {
        return "Hello, this is a secure HTTPS server!";
    }

    @GetMapping
    public ResponseEntity<?> webhookSubscribe(@RequestParam Map<String,String> allRequestParams, ModelMap model) {

        LOGGER.info("webhook GET received");

        String mode = allRequestParams.get("hub.mode");
        String verifyToken = allRequestParams.get("hub.verify_token");
        String challenge = allRequestParams.get("hub.challenge");

        if ("subscribe".equals(mode) && configLoader.getVerifyToken().equals(verifyToken)) {
            LOGGER.info("webhook VERIFIED");
            return ResponseEntity.ok(challenge);
        } else
            return ResponseEntity.status(403).build();

    }

    @PostMapping
    public ResponseEntity<?> webhookReceive(@RequestBody Object receivedData) {

        LOGGER.info("webhook POST");
        LOGGER.info(receivedData.toString());

        return ResponseEntity.ok().build();
    }

}
