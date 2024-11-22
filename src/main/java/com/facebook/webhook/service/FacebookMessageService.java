package com.facebook.webhook.service;

import com.facebook.webhook.template.*;
import com.facebook.webhook.template.model.*;
import com.restfb.*;
import com.restfb.types.send.*;
import com.restfb.types.send.Message;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.CollectionUtils;

import java.lang.*;
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

    public boolean processPostback(String recipientId, String postbackPayload) {
        try {
            List<MessageTemplate> messageTemplateList = templateManager.getMessageTemplate("rigas-meistars");

            MessageTemplate messageTemplate = messageTemplateList.stream()
                    .filter(template -> template.getPostback().equals(postbackPayload))
                    .findFirst().orElseThrow(new TemplateNotFoundException("Postback not found"));

            processAndSendMessage(messageTemplate, recipientId);
            return true;
        } catch (TemplateNotFoundException e) {
            logger.error("Message with postback {} not found", postbackPayload);
            return false;
        }
    }

    public void processAndSendMessage(MessageTemplate messageTemplate, String recipientId) {
        try {
            if (messageTemplate == null) {
                List<MessageTemplate> messageTemplateList = templateManager.getMessageTemplate("rigas-meistars");
                // Get the correct template
                messageTemplate = messageTemplateList.get(0);
            }

            // Create a textMessage with the text you want to send
            Message textMessage = new Message(messageTemplate.getMessage());
            // Create a recipient with the recipient's ID
            IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
            List<Message> messageList = new ArrayList<>();
            messageList.add(textMessage);

            // Create a textMessage with buttons
            if (!CollectionUtils.isEmpty(messageTemplate.getButtons()))
                messageList.add(createButtonGenericTemplateMessage(messageTemplate.getButtons(), messageTemplate.getButtonText()));

            if (!CollectionUtils.isEmpty(messageTemplate.getQuickReplies()))
                messageList.get(messageList.size() - 1).addQuickReplies(messageTemplate.getQuickReplies());

            publishMultipleMessagesToFacebook(recipient, messageList);

        } catch (Exception e) {
            logger.error("Failed to send welcome message", e);
//            sendErrorMessage(recipientId);
        }
    }

    // Create generic template for buttons. If more than 3 buttons then they are created as separate cards that can be swiped through
    public Message createButtonGenericTemplateMessage(List<ButtonSend> buttonList, String buttonText) {
        // Create a generic template payload
        GenericTemplatePayload genericPayload = new GenericTemplatePayload();

        List<AbstractButton> abstractButtonList = convertButtonsForSending(buttonList);
        populateGenericTemplateWithButtons(genericPayload, abstractButtonList, buttonText);

        TemplateAttachment templateAttachment = new TemplateAttachment(genericPayload);

        return new Message(templateAttachment);
    }

    public List<AbstractButton> convertButtonsForSending(List<ButtonSend> buttonList) {
        List<AbstractButton> abstractButtonList = new ArrayList<>();
        for (ButtonSend button : buttonList) {
            AbstractButton abstractButton;
            if ("postback".equals(button.getType())) {
                abstractButton = new PostbackButton(button.getTitle(), button.getPayload());
            } else {
                abstractButton = new WebButton(button.getTitle(), button.getUrl());
            }
            abstractButtonList.add(abstractButton);
        }
        return abstractButtonList;
    }

    public void populateGenericTemplateWithButtons(GenericTemplatePayload genericTemplatePayload, List<AbstractButton> abstractButtonList, String buttonText) {

        Bubble bubble = new Bubble(buttonText);
        for (AbstractButton button : abstractButtonList) {
            bubble.addButton(button);

            if (bubble.getButtons().size() == 3) {
                genericTemplatePayload.addBubble(bubble);
                bubble = new Bubble(buttonText);
            }
        }

        if (!CollectionUtils.isEmpty(bubble.getButtons()))
            genericTemplatePayload.addBubble(bubble);
    }

    public void publishMessageToFacebook(IdMessageRecipient recipient, Message message) {
        facebookClient.publish("me/messages", SendResponse.class,
                Parameter.with("recipient", recipient),
                Parameter.with("message", message));
    }

    public void publishMultipleMessagesToFacebook(IdMessageRecipient recipient, List<Message> messageList) {
        messageList.forEach(message -> publishMessageToFacebook(recipient, message));
    }
}
