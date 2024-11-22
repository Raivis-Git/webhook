package com.facebook.webhook.service;

import com.restfb.*;
import com.restfb.json.*;
import com.restfb.types.*;
import com.restfb.types.send.*;
import com.restfb.types.send.Message;
import com.restfb.types.webhook.*;
import com.restfb.types.webhook.messaging.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class FacebookService {

    Logger logger = LoggerFactory.getLogger(FacebookService.class);
    private final FacebookClient facebookClient;
    @Autowired
    private FacebookMessageService facebookMessageService;

    @Autowired
    public FacebookService(FacebookClient facebookClient) {
        this.facebookClient = facebookClient;
    }

    public void processIncomingMessage(String facebookJson) {
        // Parse the message using RestFB
        JsonMapper jsonMapper = new DefaultJsonMapper();
        WebhookObject webhookObject = jsonMapper.toJavaObject(facebookJson, WebhookObject.class);

        for (WebhookEntry entry : webhookObject.getEntryList()) {
            for (MessagingItem item : entry.getMessaging()) {
                String senderId = item.getSender().getId();
                if (sendPostBack(item, senderId) || item.getMessage() == null || item.getMessage().getText() == null)
                    continue;

                String receivedMessage = item.getMessage().getText();

                handleCommand(senderId, receivedMessage);
                // Respond with a simple message
//                sendTextMessage(senderId, "You said: " + receivedMessage);
            }
        }
    }

    public boolean sendPostBack(MessagingItem messagingItem, String senderId) {
        if (messagingItem.getPostback() == null)
            return false;

        String payload = messagingItem.getPostback().getPayload();
        logger.info("Postback received with payload: " + payload);

        return facebookMessageService.processPostback(senderId, payload);
    }

    private void handleCommand(String senderId, String command) {
        if (!command.startsWith("/"))
            return;

        // Handle different commands
        switch (command) {
            case "/start" ->
                    facebookMessageService.processPostback(senderId, "START");
            case "/help" ->
                    facebookMessageService.publishMessageToFacebook(senderId, "Write /start to start the conversation");
            default ->
                    facebookMessageService.publishMessageToFacebook(senderId, "Sorry, I don't understand that command. Type /help to see the available commands.");
        }
    }

    private void handleUserMessage(String senderId, String message) {
        // Handle user's freeform messages
        sendTextMessage(senderId, "You said: " + message);
    }

    private void sendTextMessage(String recipientId, String messageText) {
        // Create a recipient with the recipient's ID
        IdMessageRecipient recipient = new IdMessageRecipient(recipientId);

        // Create a message with the text you want to send
        Message message = new Message(messageText);

        // Send the message using the publish method
        SendResponse response = facebookClient.publish("me/messages", SendResponse.class,
                Parameter.with("recipient", recipient),
                Parameter.with("message", message));
        logger.info(response.getResult());

//        facebookMessageService.processAndSendMessage(recipientId);

//        testSendAll(recipientId);

//        Message message = new Message(messageText);
//        facebookClient.publish(recipientId + "/messages", JsonObject.class,
//                Parameter.with("message", message));
//        logger.info("Facebook API Request URL: {}", facebookClient.getWebRequestor().getDebugHeaderInfo().getDebug());
    }

    private void testSendAll(String recipientId) {

        FacebookType response = facebookClient.publish("me/messages", FacebookType.class,
                Parameter.with("recipient", new JsonObject().add("id", recipientId)),
                Parameter.with("message", new JsonObject()
                        .add("attachment", new JsonObject()
                                .add("type", "template")
                                .add("payload", new JsonObject()
                                        .add("template_type", "button")
                                        .add("text", "Select an option")
                                        .add("buttons", new JsonArray()
                                                .add(new JsonObject()
                                                        .add("type", "web_url")
                                                        .add("url", "https://example.com")
                                                        .add("title", "Visit Website")
                                                )
                                                .add(new JsonObject()
                                                        .add("type", "postback")
                                                        .add("title", "More Info")
                                                        .add("payload", "CUSTOM_PAYLOAD")
                                                )
                                        )
                                )
                        )
                        .add("quick_replies", new JsonArray()
                                .add(new JsonObject()
                                        .add("content_type", "text")
                                        .add("title", "Option 1")
                                        .add("payload", "OPTION1_PAYLOAD")
                                )
                                .add(new JsonObject()
                                        .add("content_type", "text")
                                        .add("title", "Option 2")
                                        .add("payload", "OPTION2_PAYLOAD")
                                )
                        )
                )
        );
        logger.info(response.toString());

        facebookClient.publish("me/messages", FacebookType.class,
                Parameter.with("recipient", new JsonObject().add("id", recipientId)),
                Parameter.with("message", new JsonObject()
                        .add("attachment", new JsonObject()
                                .add("type", "template")
                                .add("payload", new JsonObject()
                                        .add("template_type", "generic")  // Use generic template instead of button
                                        .add("elements", new JsonArray()
                                                .add(new JsonObject()
                                                        .add("title", "Your Title")
                                                        .add("subtitle", "Your main text message here")
                                                        .add("buttons", new JsonArray()
                                                                .add(new JsonObject()
                                                                        .add("type", "web_url")
                                                                        .add("url", "https://example.com")
                                                                        .add("title", "Visit Website")
                                                                )
                                                                .add(new JsonObject()
                                                                        .add("type", "postback")
                                                                        .add("title", "More Info")
                                                                        .add("payload", "CUSTOM_PAYLOAD")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .add("quick_replies", new JsonArray()
                                .add(new JsonObject()
                                        .add("content_type", "text")
                                        .add("title", "Option 1")
                                        .add("payload", "OPTION1_PAYLOAD")
                                )
                                .add(new JsonObject()
                                        .add("content_type", "text")
                                        .add("title", "Option 2")
                                        .add("payload", "OPTION2_PAYLOAD")
                                )
                        )
                )
        );
    }

    private void sendTextAndButtonMessage(String recipientId, String messageText, String[] buttonLabels) {
        Message message = new Message(messageText);
        List<QuickReply> quickReplies = new ArrayList<>();
        for (String label : buttonLabels) {
            QuickReply quickReply = new QuickReply(label, label);
            quickReplies.add(quickReply);
        }
        message.addQuickReplies(quickReplies);
        facebookClient.publish(recipientId + "/messages", JsonObject.class,
                Parameter.with("message", message));
        logger.info("Facebook API Request URL: {}", facebookClient.getWebRequestor().getDebugHeaderInfo().getDebug());
    }

    // Method to send message using RestFB
    public void sendTextMessage1(String recipientId, String messageText) {
        // Create a recipient with the recipient's ID
        IdMessageRecipient recipient = new IdMessageRecipient(recipientId);

        // Create a message with the text you want to send
        Message message = new Message(messageText);

        // Send the message using the publish method
        try {
            SendResponse response = facebookClient.publish("me/messages", SendResponse.class,
                    Parameter.with("recipient", recipient),
                    Parameter.with("message", message));

            System.out.println("Message sent with ID: " + response.getMessageId());
        } catch (Exception e) {
            logger.error("Message couldn't be sent");
            logger.error(e.getMessage());
        }
    }
}
