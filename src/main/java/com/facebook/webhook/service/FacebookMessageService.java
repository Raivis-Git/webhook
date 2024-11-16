package com.facebook.webhook.service;

import com.facebook.webhook.template.*;
import com.facebook.webhook.template.model.*;
import com.restfb.*;
import com.restfb.types.send.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class FacebookMessageService {

    Logger logger = LoggerFactory.getLogger(FacebookMessageService.class);
    private final TemplateManager templateManager;
    private final FacebookClient facebookClient;

    @Autowired
    public FacebookMessageService(TemplateManager templateManager, FacebookClient facebookClient) {
        this.templateManager = templateManager;
        this.facebookClient = facebookClient;
    }

//    public void sendServiceCarousel(String recipientId) {
//        try {
//            MessageTemplate template = templateManager.getMessageTemplate();
//
//            // Create the attachment payload for generic template
//            JsonObject payload = new JsonObject();
//            payload.add("template_type", "generic");
//
//            JsonArray elements = new JsonArray();
//
//            // Convert template elements to Facebook format
//            for (GenericTemplateElement item : template.getGenericTemplateElements()) {
//                JsonObject element = new JsonObject();
//                element.add("title", item.getTitle());
//                element.add("subtitle", item.getSubtitle());
//                element.add("image_url", item.getImageUrl());
//
//                // Add buttons
//                JsonArray buttons = new JsonArray();
//                for (Button btn : item.getButtons()) {
//                    JsonObject button = new JsonObject();
//                    button.add("type", btn.getType());
//                    button.add("title", btn.getTitle());
//
//                    if ("postback".equals(btn.getType())) {
//                        button.add("payload", btn.getPayload());
//                    } else if ("web_url".equals(btn.getType())) {
//                        button.add("url", btn.getUrl());
//                    }
//
//                    buttons.add(button);
//                }
//                element.add("buttons", buttons);
//                elements.add(element);
//            }
//
//            payload.add("elements", elements);
//
//            // Create the attachment
//            JsonObject attachment = new JsonObject();
//            attachment.add("type", "template");
//            attachment.add("payload", payload);
//
//            // Create the message
//            JsonObject message = new JsonObject();
//            message.add("attachment", attachment);
//
//            // Send the message using RestFB
//            facebookClient.publish("me/messages",
//                    SendResponse.class,
//                    Parameter.with("recipient", new JsonObject().add("id", recipientId)),
//                    Parameter.with("message", message));
//
//        } catch (Exception e) {
//            logger.error("Failed to send service carousel", e);
//            sendErrorMessage(recipientId);
//        }
//    }

    public void sendWelcomeMessage(String recipientId) {
        try {
            List<MessageTemplate> template = templateManager.getMessageTemplate();

            // Create message object
//            JsonObject message = new JsonObject();
//            message.add("text", template.getMessage());
//
//            // Add quick replies if present
//            if (template.getQuickReplies() != null && !template.getQuickReplies().isEmpty()) {
//                JsonArray quickReplies = new JsonArray();
//
//                for (QuickReply qr : template.getQuickReplies()) {
//                    JsonObject quickReply = new JsonObject();
//                    quickReply.add("content_type", "text");
//                    quickReply.add("title", qr.getTitle());
//                    quickReply.add("payload", qr.getPayload());
//                    quickReplies.add(quickReply);
//                }
//
//                message.add("quick_replies", quickReplies);
//            }

            // Create a recipient with the recipient's ID
            IdMessageRecipient recipient = new IdMessageRecipient(recipientId);

            MessageTemplate template1 = template.get(0);

            // Create a message with the text you want to send
            Message message = new Message(template1.getMessage());
            message.addQuickReplies(template1.getQuickReplies());

            // Send message
//            facebookClient.publish(recipientId + "/messages",
//                    SendResponse.class,
//                    Parameter.with("recipient", new JsonObject().add("id", template1.getPageId())),
//                    Parameter.with("message", template1.getMessage()));

            SendResponse response2 = facebookClient.publish("me/messages", SendResponse.class,
                    Parameter.with("recipient", recipient),
                    Parameter.with("message", message));
            logger.info("Response2: " + response2.getResult());

        } catch (Exception e) {
            logger.error("Failed to send welcome message", e);
//            sendErrorMessage(recipientId);
        }
    }

//    private void sendErrorMessage(String recipientId) {
//        try {
//            MessageTemplate errorTemplate = templateManager.getMessageTemplate();
//
//            JsonObject message = new JsonObject();
//            message.add("text", errorTemplate.getMessage());
//
//            facebookClient.publish("me/messages",
//                    SendResponse.class,
//                    Parameter.with("recipient", new JsonObject().add("id", recipientId)),
//                    Parameter.with("message", message));
//        } catch (Exception e) {
//            logger.error("Failed to send error message", e);
//        }
//    }
}
