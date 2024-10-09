package com.facebook.webhook.controller;

import com.facebook.webhook.config.ConfigLoader;
import com.restfb.*;
import com.restfb.types.send.*;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;


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

                    if (item.getPostback() != null) {
                        String payload = item.getPostback().getPayload();
                        System.out.println("Postback received with payload: " + payload);

                        // Respond based on payload
                        if ("GET_STARTED_PAYLOAD".equals(payload)) {
                            sendTextMessage(senderId, "Welcome! How can I help you?");
                        } else if ("MORE_INFO_PAYLOAD".equals(payload)) {
                            sendTextMessage(senderId, "Here's more information about our services...");
                        }
                    } else {
                        if ("buttons".equals(receivedMessage.trim().toLowerCase(Locale.ROOT)))
                            sendButtonTemplate(senderId);
                        else
                            // Respond with a simple message
                            sendTextMessage(senderId, "You said: " + receivedMessage);
                    }
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

    public void sendButtonTemplate(String recipientId) {
        FacebookClient facebookClient = new DefaultFacebookClient("your_page_access_token", Version.LATEST);

        // Create recipient
        IdMessageRecipient recipient = new IdMessageRecipient(recipientId);

        // Create buttons
        PostbackButton button1 = new PostbackButton("Get Started", "GET_STARTED_PAYLOAD");
        PostbackButton button2 = new PostbackButton("More Info", "MORE_INFO_PAYLOAD");

        // Create button template payload
        ButtonTemplatePayload buttonPayload = new ButtonTemplatePayload("Choose an option:");
        buttonPayload.addButton(button1);
        buttonPayload.addButton(button2);

        // Wrap the button template in a TemplateAttachment
        TemplateAttachment templateAttachment = new TemplateAttachment(buttonPayload);

        // Create a message with the button template
        Message message = new Message(templateAttachment);

        // Send the message
        SendResponse response = facebookClient.publish("me/messages", SendResponse.class,
                Parameter.with("recipient", recipient),
                Parameter.with("message", message));

        System.out.println("Message sent with ID: " + response.getMessageId());
    }
}
