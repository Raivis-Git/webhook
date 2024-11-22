package com.facebook.webhook.template.model;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.annotations.*;
import com.restfb.types.send.QuickReply;
import com.restfb.types.whatsapp.platform.message.*;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageTemplate {
    @SerializedName("pageId")
    private String pageId;
    @SerializedName("pageName")
    private String pageName;
    @SerializedName("message")
    private String message;
    @SerializedName("messagingType")
    private String messagingType;
    @SerializedName("postback")
    private String postback;
    @SerializedName("buttonText")
    private String buttonText;
    @SerializedName("buttons")
    private List<ButtonSend> buttons;
    @SerializedName("quickReplies")
    private List<QuickReply> quickReplies;
    @SerializedName("genericTemplateElements")
    private List<GenericTemplateElement> genericTemplateElements;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessagingType() {
        return messagingType;
    }

    public void setMessagingType(String messagingType) {
        this.messagingType = messagingType;
    }

    public String getPostback() {
        return postback;
    }

    public void setPostback(String postback) {
        this.postback = postback;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public List<ButtonSend> getButtons() {
        return buttons;
    }

    public void setButtons(List<ButtonSend> buttons) {
        this.buttons = buttons;
    }

    public List<QuickReply> getQuickReplies() {
        return quickReplies;
    }

    public void setQuickReplies(List<QuickReply> quickReplies) {
        this.quickReplies = quickReplies;
    }

    public List<GenericTemplateElement> getGenericTemplateElements() {
        return genericTemplateElements;
    }

    public void setGenericTemplateElements(List<GenericTemplateElement> genericTemplateElements) {
        this.genericTemplateElements = genericTemplateElements;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }
}
