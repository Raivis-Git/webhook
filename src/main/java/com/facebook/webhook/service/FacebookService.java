package com.facebook.webhook.service;

import com.restfb.*;
import com.restfb.json.*;
import com.restfb.types.send.*;
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
                if (item.getMessage() == null || item.getMessage().getText() == null)
                    return;
                String senderId = item.getSender().getId();
                String receivedMessage = item.getMessage().getText();

                if (sendPostBack(item, senderId))
                    continue;

                handleCommand(senderId, receivedMessage);
                // Respond with a simple message
                sendTextMessage(senderId, "You said: " + receivedMessage);
            }
        }
    }

    public boolean sendPostBack(MessagingItem messagingItem, String senderId) {
        if (messagingItem.getPostback() == null)
            return false;

        String payload = messagingItem.getPostback().getPayload();
        System.out.println("Postback received with payload: " + payload);

        //Respond based on payload
        if ("GET_STARTED_PAYLOAD".equals(payload)) {
            sendTextMessage(senderId, "Welcome! How can I help you?");
            return true;
        } else if ("MORE_INFO_PAYLOAD".equals(payload)) {
            sendTextMessage(senderId, "Here's more information about our services...");
            return true;
        }
        return false;
    }

    private void handleCommand(String senderId, String command) {
        if (!command.startsWith("/"))
            return;

        // Handle different commands
        switch (command) {
            case "/help" ->
                    sendTextMessage(senderId, "Here are the available commands:\n/help - Show this menu\n/info - Get more information");
            case "/info" ->
                    sendTextAndButtonMessage(senderId, "Here is some information about our chatbot:", new String[]{"More Details", "Contact Us"});
            default ->
                    sendTextMessage(senderId, "Sorry, I don't understand that command. Type /help to see the available commands.");
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

        facebookMessageService.sendWelcomeMessage(recipientId);

//        Message message = new Message(messageText);
//        facebookClient.publish(recipientId + "/messages", JsonObject.class,
//                Parameter.with("message", message));
//        logger.info("Facebook API Request URL: {}", facebookClient.getWebRequestor().getDebugHeaderInfo().getDebug());
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

        sendButtonTemplate1(recipientId);
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

    public void sendButtonTemplate1(String recipientId) {
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
