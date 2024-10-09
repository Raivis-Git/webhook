package com.facebook.webhook.controller;

import com.facebook.webhook.config.ConfigLoader;
import com.restfb.*;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.SendResponse;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/facebook/webhook")
public class WebhookController {

    Logger LOGGER = LoggerFactory.getLogger(WebhookController.class);

    @Value("${verify.token}")
    private String verifyToken;

    @Value("${page.access.token}")
    private String pageAccessToken;

    @Autowired
    ConfigLoader configLoader;

    @GetMapping("/test")
    public String test() {
        return "Hello, this is a secure HTTPS server! " + verifyToken;
    }

    @GetMapping
    public ResponseEntity<?> webhookSubscribe(@RequestBody String requestBody,
                                              @RequestParam("hub.verify_token") String verifyToken,
                                              @RequestParam("hub.challenge") String challenge,
                                              @RequestParam("hub.mode") String mode) {

        LOGGER.info("webhook GET received");

        if ("subscribe".equals(mode) && configLoader.getVerifyToken().equals(verifyToken)) {
            LOGGER.info("webhook VERIFIED");
            return ResponseEntity.ok(challenge);
        } else
            return ResponseEntity.status(403).build();

    }

    @PostMapping // Process messages from Facebook
    public void processMessage(@RequestBody String requestBody) {
        LOGGER.info("POST /webhook received \n" + requestBody);
        // Parse the message using RestFB
        JsonMapper jsonMapper = new DefaultJsonMapper();
        WebhookObject webhookObject = jsonMapper.toJavaObject(requestBody, WebhookObject.class);

        for (WebhookEntry entry : webhookObject.getEntryList()) {
            for (MessagingItem item : entry.getMessaging()) {
                if (item.getMessage() != null && item.getMessage().getText() != null) {
                    String senderId = item.getSender().getId();
                    String receivedMessage = item.getMessage().getText();

                    // Respond with a simple message
                    sendTextMessage(senderId, "You said: " + receivedMessage);
                }
            }
        }
    }

    // Method to send message using RestFB
    public void sendTextMessage(String recipientId, String messageText) {
        FacebookClient facebookClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);

        // Create a recipient with the recipient's ID
        IdMessageRecipient recipient = new IdMessageRecipient(recipientId);

        // Create a message with the text you want to send
        Message message = new Message(messageText);

        // Send the message using the publish method
        SendResponse response = facebookClient.publish("me/messages", SendResponse.class,
                Parameter.with("recipient", recipient),
                Parameter.with("message", message));

        System.out.println("Message sent with ID: " + response.getMessageId());
    }
}
